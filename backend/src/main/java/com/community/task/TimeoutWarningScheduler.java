package com.community.task;

import com.community.service.TimeoutWarnService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TimeoutWarningScheduler {
    private final TimeoutWarnService timeoutWarnService;
    private final RedisTemplate<String, Object> redisTemplate;

    public TimeoutWarningScheduler(TimeoutWarnService timeoutWarnService, RedisTemplate<String, Object> redisTemplate) {
        this.timeoutWarnService = timeoutWarnService;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "${app.warning.cron:0 0 9 * * *}")
    public void createSystemWarnings() {
        String lockKey = "lock:timeout-warning-scan";
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofMinutes(30));
        if (!Boolean.TRUE.equals(locked)) {
            return;
        }
        try {
            timeoutWarnService.createSystemWarnings();
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
