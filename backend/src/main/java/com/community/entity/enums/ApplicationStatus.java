package com.community.entity.enums;

import com.community.common.BusinessException;

import java.util.Set;

/** The only states persisted for a service application. */
public enum ApplicationStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    REJECTED;

    public static ApplicationStatus from(String value) {
        try {
            return value == null ? null : valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "非法申请状态");
        }
    }

    public static ApplicationStatus auditResult(String value, Set<ApplicationStatus> allowed) {
        ApplicationStatus result = from(value);
        if (result == null || !allowed.contains(result)) {
            throw new BusinessException(400, "非法审核结果");
        }
        return result;
    }
}
