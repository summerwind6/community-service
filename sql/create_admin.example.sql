-- Local/bootstrap use only. Replace every placeholder before executing, then delete your local copy.
SET @username = 'replace-admin-username';
SET @password = 'replace-with-a-strong-password';
SET @salt = REPLACE(UUID(), '-', '');
SET @password_hash = LOWER(MD5(UNHEX(MD5(CONCAT(@salt, @password)))));

INSERT INTO sys_user (username, password, salt, real_name, status)
VALUES (@username, @password_hash, @salt, 'System Administrator', 1);

SET @user_id = LAST_INSERT_ID();
INSERT INTO sys_user_role (user_id, role_id) VALUES (@user_id, 1);
