package com.community.audit;

import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;

import java.time.Instant;

public record AuditEvent(String eventId, int schemaVersion, Long applicationId, Long auditRecordId,
                         String auditType, String auditResult, int applicationVersion, Instant occurredAt) {
    public static final String TOPIC = "community.application.audit.v1";
    public static final String DLT = TOPIC + ".DLT";
    public static final String GROUP = "community-notification-v1";

    public static AuditEvent from(ApplicationForm form, AuditRecord record) {
        return new AuditEvent("audit-v1-" + record.getId(), 1, form.getId(), record.getId(),
                record.getAuditType(), record.getAuditResult(), form.getVersion(), Instant.now());
    }
}
