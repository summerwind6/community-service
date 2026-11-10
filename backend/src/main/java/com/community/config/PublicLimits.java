package com.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class PublicLimits {
    private final int progressIpLimit;
    private final int applyPhoneLimit;
    private final int applyIpLimit;
    private final int publicUploadIpLimit;
    private final int evaluationIpLimit;

    public PublicLimits(@Value("${app.public.progress-ip-limit-per-minute:10}") int progressIpLimit,
                        @Value("${app.public.apply-phone-limit-per-hour:3}") int applyPhoneLimit,
                        @Value("${app.public.apply-ip-limit-per-hour:20}") int applyIpLimit,
                        @Value("${app.public.upload-ip-limit-per-minute:20}") int publicUploadIpLimit,
                        @Value("${app.public.evaluation-ip-limit-per-minute:10}") int evaluationIpLimit) {
        this.progressIpLimit = Math.max(1, Math.min(progressIpLimit, 10_000));
        this.applyPhoneLimit = Math.max(1, Math.min(applyPhoneLimit, 10_000));
        this.applyIpLimit = Math.max(1, Math.min(applyIpLimit, 10_000));
        this.publicUploadIpLimit = Math.max(1, Math.min(publicUploadIpLimit, 10_000));
        this.evaluationIpLimit = Math.max(1, Math.min(evaluationIpLimit, 10_000));
    }

    public int progressIpLimit() {
        return progressIpLimit;
    }

    public int applyPhoneLimit() {
        return applyPhoneLimit;
    }

    public int applyIpLimit() {
        return applyIpLimit;
    }

    public int publicUploadIpLimit() {
        return publicUploadIpLimit;
    }

    public int evaluationIpLimit() {
        return evaluationIpLimit;
    }
}
