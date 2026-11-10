package com.community.task;

import com.community.cache.RedisLease;
import com.community.service.TimeoutWarnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class TimeoutWarningScheduler {
    private static final Logger log = LoggerFactory.getLogger(TimeoutWarningScheduler.class);
    private static final String LOCK = "lock:timeout-warning-scan";
    private final TimeoutWarnService timeoutWarnService;
    private final StringRedisTemplate redis;

    public TimeoutWarningScheduler(TimeoutWarnService timeoutWarnService, StringRedisTemplate redis) {
        this.timeoutWarnService = timeoutWarnService;
        this.redis = redis;
    }

    @Scheduled(cron = "${app.warning.cron:0 0 9 * * *}")
    public void createSystemWarnings() {
        String owner = UUID.randomUUID().toString();
        Duration lease = Duration.ofMinutes(30);
        if (!Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(LOCK, owner, lease))) {
            return;
        }
        long startedAt = System.nanoTime();
        try {
            timeoutWarnService.createSystemWarnings();
        } finally {
            if (System.nanoTime() - startedAt >= lease.toNanos()) {
                log.warn("Timeout warning task exceeded its Redis lease");
            }
            try {
                RedisLease.release(redis, LOCK, owner);
            } catch (DataAccessException e) {
                log.warn("Unable to release timeout warning lease", e);
            }
        }
    }
}
