package com.community.cache;

import com.community.common.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class PublicListCache {
    private static final Logger log = LoggerFactory.getLogger(PublicListCache.class);
    private static final DefaultRedisScript<Long> FILL = new DefaultRedisScript<>("""
            if redis.call('GET', KEYS[3]) == ARGV[1]
                and (redis.call('GET', KEYS[2]) or '0') == ARGV[2] then
                redis.call('SET', KEYS[1], ARGV[3], 'EX', ARGV[4])
                return 1
            end
            return 0
            """, Long.class);
    private static final DefaultRedisScript<Long> INVALIDATE = new DefaultRedisScript<>("""
            redis.call('INCR', KEYS[2])
            return redis.call('DEL', KEYS[1])
            """, Long.class);
    private static final int INVALIDATION_RETRIES = 3;

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final CacheSettings settings;
    private final MeterRegistry metrics;
    private final Semaphore dbBudget;
    private final ConcurrentHashMap<String, Invalidation> pending = new ConcurrentHashMap<>();
    private volatile long circuitUntil;

    public PublicListCache(StringRedisTemplate redis, ObjectMapper mapper, CacheSettings settings,
                           MeterRegistry metrics,
                           @Value("${app.cache.db-concurrency:4}") int concurrency) {
        this.redis = redis;
        this.mapper = mapper;
        this.settings = settings;
        this.metrics = metrics;
        this.dbBudget = new Semaphore(Math.max(1, Math.min(concurrency, 32)));
    }

    public <T> List<T> read(String key, Class<T> type, Supplier<List<T>> load) {
        if (!settings.enabled() || System.currentTimeMillis() < circuitUntil || pending.containsKey(key)) {
            return query(key, load);
        }
        String lock = key + ":lock";
        String owner = UUID.randomUUID().toString();
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(400);
        boolean acquired = false;
        try {
            do {
                List<T> hit = cached(key, type);
                if (hit != null) {
                    return hit;
                }
                try {
                    acquired = Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lock, owner, Duration.ofSeconds(5)));
                } catch (DataAccessException e) {
                    throw new CacheAccessException(e);
                }
                if (acquired) {
                    return rebuild(key, lock, owner, type, load);
                }
                TimeUnit.MILLISECONDS.sleep(25);
            } while (System.nanoTime() < deadline);
            throw busy();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw busy();
        } catch (CacheAccessException e) {
            openCircuit(key, e);
            return query(key, load);
        } finally {
            if (acquired) {
                try {
                    RedisLease.release(redis, lock, owner);
                } catch (DataAccessException e) {
                    openCircuit(key, e);
                }
            }
        }
    }

    private <T> List<T> rebuild(String key, String lock, String owner, Class<T> type, Supplier<List<T>> load) {
        String generation;
        try {
            generation = redis.opsForValue().get(key + ":generation");
        } catch (DataAccessException e) {
            throw new CacheAccessException(e);
        }
        List<T> hit = cached(key, type);
        if (hit != null) {
            return hit;
        }
        List<T> result = query(key, load);
        try {
            redis.execute(FILL, List.of(key, key + ":generation", lock), owner,
                    generation == null ? "0" : generation, mapper.writeValueAsString(result),
                    Integer.toString(settings.ttlSeconds() + ThreadLocalRandom.current().nextInt(61)));
        } catch (DataAccessException | JsonProcessingException e) {
            openCircuit(key, e);
        }
        return result;
    }

    private <T> List<T> cached(String key, Class<T> type) {
        try {
            String value = redis.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            List<T> result = mapper.readValue(value, listType);
            metrics.counter("community.cache.hit", "key", key).increment();
            return result;
        } catch (DataAccessException | JsonProcessingException e) {
            throw new CacheAccessException(e);
        }
    }

    private <T> List<T> query(String key, Supplier<List<T>> load) {
        if (!dbBudget.tryAcquire()) {
            metrics.counter("community.cache.db.rejected", "key", key).increment();
            throw busy();
        }
        try {
            metrics.counter("community.cache.db.query", "key", key).increment();
            return load.get();
        } finally {
            dbBudget.release();
        }
    }

    public void invalidateAfterCommit(String key) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    invalidate(key);
                }
            });
        } else {
            invalidate(key);
        }
    }

    private void invalidate(String key) {
        Invalidation invalidation = new Invalidation(INVALIDATION_RETRIES);
        pending.put(key, invalidation);
        tryInvalidate(key, invalidation);
    }

    @Scheduled(fixedDelay = 2_000)
    void retryInvalidations() {
        pending.forEach(this::tryInvalidate);
    }

    private void tryInvalidate(String key, Invalidation invalidation) {
        synchronized (invalidation) {
            if (pending.get(key) != invalidation) {
                return;
            }
            try {
                redis.execute(INVALIDATE, List.of(key, key + ":generation"));
                pending.remove(key, invalidation);
            } catch (DataAccessException e) {
                metrics.counter("community.cache.invalidation.failed", "key", key).increment();
                openCircuit(key, e);
                if (invalidation.retries-- == 0) {
                    pending.remove(key, invalidation);
                    log.error("Cache invalidation exhausted retries for {}", key);
                }
            }
        }
    }

    private void openCircuit(String key, Exception cause) {
        long now = System.currentTimeMillis();
        if (now >= circuitUntil) {
            log.warn("Cache temporarily bypassed for {} ({})", key, cause.getClass().getSimpleName());
            circuitUntil = now + 2_000;
        }
    }

    private static BusinessException busy() {
        return new BusinessException(503, "查询繁忙，请稍后重试");
    }

    private static final class Invalidation {
        private int retries;

        private Invalidation(int retries) {
            this.retries = retries;
        }
    }

    private static final class CacheAccessException extends RuntimeException {
        private CacheAccessException(Exception cause) {
            super(cause);
        }
    }
}
