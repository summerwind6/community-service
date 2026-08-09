package com.community.service;

public interface PublicRateLimitService {
    void check(String scope, String subject, int maxRequests, long windowSeconds);
}
