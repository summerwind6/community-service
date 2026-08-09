-- 本地或首次部署使用。复制本文件后替换全部占位符再执行，执行完成后删除本地副本。
-- 请先执行 init.sql；本脚本会为新用户分配超级管理员角色（role_id = 1）。
SET @username = 'replace-admin-username';
SET @password = 'replace-with-a-strong-password';
SET @salt = REPLACE(UUID(), '-', '');
SET @password_hash = LOWER(MD5(UNHEX(MD5(CONCAT(@salt, @password)))));

INSERT INTO sys_user (username, password, salt, real_name, status)
VALUES (@username, @password_hash, @salt, 'System Administrator', 1);

SET @user_id = LAST_INSERT_ID();
INSERT INTO sys_user_role (user_id, role_id) VALUES (@user_id, 1);
