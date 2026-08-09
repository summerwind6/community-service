-- Apply once to databases created before the security and timeout-warning upgrade.
UPDATE application_form SET status = 'COMPLETED' WHERE status = 'APPROVED';
-- Rotate the old public demonstration credential before production use.
UPDATE sys_user SET status = 0 WHERE username = 'admin' AND salt = 'salt123';
ALTER TABLE application_form
    MODIFY apply_no VARCHAR(50) NULL,
    ADD COLUMN version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号' AFTER remark,
    ADD COLUMN last_warn_time DATETIME NULL COMMENT '最后一次系统预警时间' AFTER version,
    ADD COLUMN warn_count INT NOT NULL DEFAULT 0 COMMENT '系统预警次数' AFTER last_warn_time;

CREATE TABLE application_warning (
    id BIGINT NOT NULL AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    warn_type VARCHAR(30) NOT NULL,
    timeout_days INT NOT NULL DEFAULT 0,
    warn_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_application_warn_type (application_id, warn_type),
    KEY idx_warn_time (warn_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请系统预警记录';

UPDATE sys_permission SET perm_name = '申请初审', perm_code = 'apply:review' WHERE id = 26;
INSERT INTO sys_permission (id, perm_name, perm_code, perm_type, parent_id, url, sort_order, status)
VALUES (37, '申请复审', 'apply:approve', 'button', 14, NULL, 2, 1),
       (38, '评价列表', 'evaluation:list', 'button', 21, NULL, 1, 1),
       (39, '指南维护', 'guide:manage', 'button', 10, NULL, 1, 1),
       (40, '预约管理', 'appointment:manage', 'button', 16, NULL, 1, 1),
       (41, '发送通知', 'notification:send', 'button', 17, NULL, 1, 1),
       (42, '导出报表', 'report:export', 'button', 22, NULL, 1, 1)
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name), perm_code = VALUES(perm_code), status = VALUES(status);
INSERT IGNORE INTO sys_role_perm (role_id, perm_id) VALUES
    (1, 37), (1, 38), (1, 39), (1, 40), (1, 41), (1, 42),
    (2, 26), (2, 38), (3, 37), (3, 38);
