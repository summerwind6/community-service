package com.community.service.impl;

import com.community.common.BusinessException;
import com.community.service.PublicRateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicRateLimitServiceImpl implements PublicRateLimitService {
    private static final Logger log = LoggerFactory.getLogger(PublicRateLimitServiceImpl.class);
    private static final DefaultRedisScript<Long> INCREMENT = new DefaultRedisScript<>("""
            local count = redis.call('INCR', KEYS[1])
            if redis.call('TTL', KEYS[1]) == -1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            return count
            """, Long.class);
    private final StringRedisTemplate redis;

    public PublicRateLimitServiceImpl(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void check(String scope, String subject, int maxRequests, long windowSeconds) {
        String key = "rate:" + scope + ":" + subject;
        Long count;
        try {
            count = redis.execute(INCREMENT, List.of(key), Long.toString(windowSeconds));
        } catch (DataAccessException e) {
            log.warn("Rate limiter unavailable for {} ({})", scope, e.getClass().getSimpleName());
            throw unavailable();
        }
        if (count == null) {
            throw unavailable();
        }
        if (count > maxRequests) {
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
    }

    private static BusinessException unavailable() {
        return new BusinessException(503, "服务暂不可用，请稍后重试");
    }
}
