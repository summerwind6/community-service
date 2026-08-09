-- Apply this only when upgrade_20260809_security_and_warning.sql has already run.
-- It adds permissions introduced by the post-upgrade authorization fix.
INSERT INTO sys_permission (id, perm_name, perm_code, perm_type, parent_id, url, sort_order, status)
VALUES (39, '指南维护', 'guide:manage', 'button', 10, NULL, 1, 1),
       (40, '预约管理', 'appointment:manage', 'button', 16, NULL, 1, 1),
       (41, '发送通知', 'notification:send', 'button', 17, NULL, 1, 1),
       (42, '导出报表', 'report:export', 'button', 22, NULL, 1, 1)
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name), perm_code = VALUES(perm_code), status = VALUES(status);

INSERT IGNORE INTO sys_role_perm (role_id, perm_id) VALUES
    (1, 37), (1, 38), (1, 39), (1, 40), (1, 41), (1, 42);
