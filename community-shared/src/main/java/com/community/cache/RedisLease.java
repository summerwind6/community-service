package com.community.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

public final class RedisLease {
    private static final DefaultRedisScript<Long> RELEASE = new DefaultRedisScript<>("""
            if redis.call('GET', KEYS[1]) == ARGV[1] then
                return redis.call('DEL', KEYS[1])
            end
            return 0
            """, Long.class);

    private RedisLease() {
    }

    public static void release(StringRedisTemplate redis, String key, String owner) {
        redis.execute(RELEASE, List.of(key), owner);
    }
}
