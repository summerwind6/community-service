-- 对已执行 upgrade_20260809.sql 的数据库执行一次。新环境直接使用 init.sql。
USE community_service;

ALTER TABLE notification
    ADD COLUMN source_event_id VARCHAR(64) DEFAULT NULL,
    ADD UNIQUE KEY uk_notification_source_event (source_event_id);

CREATE TABLE IF NOT EXISTS application_event_outbox (
    event_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    audit_record_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    attempts INT NOT NULL DEFAULT 0,
    next_attempt_at DATETIME(6) NOT NULL COMMENT '重试时间；SENDING 时为租约到期时间（UTC）',
    lease_owner VARCHAR(36) DEFAULT NULL,
    created_at DATETIME(6) NOT NULL,
    sent_at DATETIME(6) DEFAULT NULL,
    last_error VARCHAR(120) DEFAULT NULL,
    PRIMARY KEY (event_id),
    UNIQUE KEY uk_outbox_audit_type (event_type, audit_record_id),
    KEY idx_outbox_due (status, next_attempt_at, event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
