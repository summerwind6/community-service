package com.community.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class CacheSettings {
    private final boolean enabled;
    private final int ttlSeconds;

    public CacheSettings(@Value("${app.cache.enabled:true}") boolean enabled,
                         @Value("${app.cache.ttl-seconds:300}") int ttlSeconds) {
        this.enabled = enabled;
        this.ttlSeconds = Math.max(5, Math.min(ttlSeconds, 3600));
    }

    public boolean enabled() {
        return enabled;
    }

    public int ttlSeconds() {
        return ttlSeconds;
    }
}
