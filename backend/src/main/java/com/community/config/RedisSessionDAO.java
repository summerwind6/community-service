package com.community.config;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisSessionDAO implements SessionDAO {

    private static final String PREFIX = "shiro:session:";
    private static final long TIMEOUT_MINUTES = 30;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Serializable create(Session session) {
        String key = PREFIX + session.getId();
        redisTemplate.opsForValue().set(key, session, TIMEOUT_MINUTES, TimeUnit.MINUTES);
        return session.getId();
    }

    @Override
    public Session readSession(Serializable sessionId) {
        return (Session) redisTemplate.opsForValue().get(PREFIX + sessionId);
    }

    @Override
    public void update(Session session) {
        String key = PREFIX + session.getId();
        redisTemplate.opsForValue().set(key, session, TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void delete(Session session) {
        redisTemplate.delete(PREFIX + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        return values.stream()
                .filter(v -> v instanceof Session)
                .map(v -> (Session) v)
                .collect(Collectors.toList());
    }
}
