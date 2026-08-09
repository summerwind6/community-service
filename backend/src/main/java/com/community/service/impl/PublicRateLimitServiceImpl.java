package com.community.service.impl;

import com.community.common.BusinessException;
import com.community.service.PublicRateLimitService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PublicRateLimitServiceImpl implements PublicRateLimitService {
    private final RedisTemplate<String, Object> redisTemplate;

    public PublicRateLimitServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void check(String scope, String subject, int maxRequests, long windowSeconds) {
        String key = "rate:" + scope + ":" + subject;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        if (count != null && count > maxRequests) {
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
    }
}
