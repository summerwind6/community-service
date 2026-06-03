-- =====================================================
-- 社区便民服务申请与进度管理系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- =====================================================

CREATE DATABASE IF NOT EXISTS `community_service` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `community_service`;
SET NAMES utf8mb4;

-- =====================================================
-- 1. 系统用户表 (sys_user)
-- =====================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    `salt` VARCHAR(50) NOT NULL COMMENT '密码盐值',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =====================================================
-- 2. 角色表 (sys_role)
-- =====================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码（唯一）',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =====================================================
-- 3. 权限表 (sys_permission)
-- =====================================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `perm_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `perm_code` VARCHAR(100) NOT NULL COMMENT '权限编码（唯一）',
    `perm_type` VARCHAR(20) DEFAULT 'menu' COMMENT '权限类型：menu-菜单 button-按钮',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID（顶级为0）',
    `url` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '权限描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- =====================================================
-- 4. 角色权限关联表 (sys_role_perm)
-- =====================================================
DROP TABLE IF EXISTS `sys_role_perm`;
CREATE TABLE `sys_role_perm` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `perm_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =====================================================
-- 5. 用户角色关联表 (sys_user_role)
-- =====================================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =====================================================
-- 6. 居民信息表 (resident_info)
-- =====================================================
DROP TABLE IF EXISTS `resident_info`;
CREATE TABLE `resident_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    `community` VARCHAR(100) DEFAULT NULL COMMENT '所属社区',
    `gender` CHAR(1) DEFAULT NULL COMMENT '性别：M-男 F-女',
    `birthday` DATE DEFAULT NULL COMMENT '出生日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='居民信息表';

-- =====================================================
-- 7. 服务项目表 (service_item)
-- =====================================================
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `item_name` VARCHAR(100) NOT NULL COMMENT '服务项目名称',
    `item_code` VARCHAR(50) NOT NULL COMMENT '项目编码（唯一）',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '服务类别',
    `description` TEXT DEFAULT NULL COMMENT '项目描述',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '费用（元）',
    `processing_days` INT DEFAULT 7 COMMENT '预计处理天数',
    `required_docs` TEXT DEFAULT NULL COMMENT '所需材料说明',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_code` (`item_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- =====================================================
-- 8. 申请单表 (application_form)
-- =====================================================
DROP TABLE IF EXISTS `application_form`;
CREATE TABLE `application_form` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `apply_no` VARCHAR(50) NOT NULL COMMENT '申请编号（唯一）',
    `resident_id` BIGINT DEFAULT NULL COMMENT '申请人ID（居民表）',
    `resident_name` VARCHAR(50) DEFAULT NULL COMMENT '申请人姓名',
    `gender` CHAR(1) DEFAULT NULL COMMENT '性别：M-男 F-女',
    `service_item_id` BIGINT NOT NULL COMMENT '服务项目ID',
    `service_item_name` VARCHAR(100) DEFAULT NULL COMMENT '服务项目名称',
    `apply_content` TEXT DEFAULT NULL COMMENT '申请内容描述',
    `attachments` TEXT DEFAULT NULL COMMENT '附件路径（JSON数组）',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING待受理/PROCESSING处理中/APPROVED通过/REJECTED驳回/COMPLETED已完成',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际费用',
    `handler_id` BIGINT DEFAULT NULL COMMENT '当前处理人ID',
    `handler_name` VARCHAR(50) DEFAULT NULL COMMENT '当前处理人姓名',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_apply_no` (`apply_no`),
    KEY `idx_resident_id` (`resident_id`),
    KEY `idx_service_item_id` (`service_item_id`),
    KEY `idx_status` (`status`),
    KEY `idx_handler_id` (`handler_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请单表';

-- =====================================================
-- 9. 审核记录表 (audit_record)
-- =====================================================
DROP TABLE IF EXISTS `audit_record`;
CREATE TABLE `audit_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `application_id` BIGINT NOT NULL COMMENT '申请单ID',
    `audit_type` VARCHAR(20) NOT NULL COMMENT '审核类型：COMMUNITY社区审核/STREET街道审核',
    `auditor_id` BIGINT NOT NULL COMMENT '审核人ID',
    `auditor_name` VARCHAR(50) DEFAULT NULL COMMENT '审核人姓名',
    `audit_result` VARCHAR(20) NOT NULL COMMENT '审核结果：APPROVED通过/REJECTED驳回/PENDING待审核',
    `audit_opinion` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    `audit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_application_id` (`application_id`),
    KEY `idx_auditor_id` (`auditor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- =====================================================
-- 10. 系统配置表 (sys_config)
-- =====================================================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键（唯一）',
    `config_value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '配置说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =====================================================
-- 11. 预约表 (appointment)
-- =====================================================
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `resident_name` VARCHAR(50) DEFAULT NULL COMMENT '预约人姓名',
    `resident_phone` VARCHAR(20) DEFAULT NULL COMMENT '预约人电话',
    `service_item_id` BIGINT DEFAULT NULL COMMENT '预约服务事项ID',
    `service_item_name` VARCHAR(100) DEFAULT NULL COMMENT '服务事项名称',
    `appointment_date` DATE DEFAULT NULL COMMENT '预约日期',
    `time_slot` VARCHAR(20) DEFAULT NULL COMMENT '时间段：AM上午/PM下午',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING待确认/CONFIRMED已确认/CANCELED已取消/COMPLETED已完成',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_appointment_date` (`appointment_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- =====================================================
-- 12. 消息通知表 (notification)
-- =====================================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT DEFAULT NULL COMMENT '通知内容',
    `type` VARCHAR(20) DEFAULT 'SYSTEM' COMMENT '类型：AUDIT审核通知/SYSTEM系统通知',
    `target_id` BIGINT DEFAULT NULL COMMENT '关联目标ID（如申请单ID）',
    `target_name` VARCHAR(50) DEFAULT NULL COMMENT '接收人姓名',
    `target_phone` VARCHAR(20) DEFAULT NULL COMMENT '接收人电话',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0未读/1已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- =====================================================
-- 13. 服务评价表 (evaluation)
-- =====================================================
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `application_id` BIGINT NOT NULL COMMENT '关联申请单ID',
    `score` INT NOT NULL COMMENT '评分（1-5星）',
    `content` VARCHAR(1000) DEFAULT NULL COMMENT '评价内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_application_id` (`application_id`),
    KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务评价表';

-- =====================================================
-- 14. 办事指南表 (guide)
-- =====================================================
DROP TABLE IF EXISTS `guide`;
CREATE TABLE `guide` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `content` TEXT DEFAULT NULL COMMENT '文章内容（富文本HTML）',
    `service_item_id` BIGINT DEFAULT NULL COMMENT '关联服务事项ID',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `status` TINYINT DEFAULT 0 COMMENT '状态：1-发布 0-草稿',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    PRIMARY KEY (`id`),
    KEY `idx_service_item_id` (`service_item_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='办事指南表';


-- =====================================================
-- 初始化种子数据
-- =====================================================

-- -----------------------------------------------------
-- 角色（3个）
-- -----------------------------------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`) VALUES
    (1, '超级管理员', 'ADMIN', '拥有系统所有权限', 1),
    (2, '社区受理员', 'CLERK', '负责受理居民的服务申请', 1),
    (3, '街道审核员', 'REVIEWER', '负责审核服务申请', 1);

-- -----------------------------------------------------
-- 权限码（ID 1-22 与前端权限树严格对应）
-- perm_type: menu=菜单容器, page=页面, button=操作按钮（button不在权限树中显示）
-- -----------------------------------------------------
INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `url`, `sort_order`, `status`) VALUES
    -- 仪表盘
    (1,  '仪表盘',       'stat:dashboard',   'menu',   0,  '/dashboard',            1, 1),
    -- 系统管理
    (2,  '系统管理',     'system',           'menu',   0,  '/system',               2, 1),
    (3,  '用户管理',     'user:list',        'page',   2,  '/system/user',          1, 1),
    (4,  '角色管理',     'role:list',        'page',   2,  '/system/role',          2, 1),
    (5,  '系统配置',     'system:config',    'page',   2,  '/system/config',        3, 1),
    (6,  '在线用户',     'system:online',    'page',   2,  '/system/online',        4, 1),
    -- 服务管理
    (7,  '服务管理',     'service',          'menu',   0,  '/service',              3, 1),
    (8,  '居民管理',     'resident:list',    'page',   7,  '/resident/list',        1, 1),
    (9,  '服务项目',     'item:list',        'page',   7,  '/service/item',         2, 1),
    (10, '办事指南管理', 'guide:list',       'page',   7,  '/service/guide',        3, 1),
    (11, '办事指南公开页','guide:public',    'page',   7,  '/service/guide-public', 4, 1),
    (12, '附件管理',     'attachment:list',  'page',   7,  '/service/attachment',   5, 1),
    -- 申请管理
    (13, '申请管理',     'apply',            'menu',   0,  '/apply',                4, 1),
    (14, '申请列表',     'apply:list',       'page',   13, '/apply/list',           1, 1),
    (15, '超时预警',     'apply:overtime',   'page',   13, '/apply/overtime',       2, 1),
    (16, '预约管理',     'appointment:list', 'page',   13, '/apply/appointment',    3, 1),
    (17, '消息通知',     'notification:list','page',   13, '/apply/notification',   4, 1),
    -- 统计分析
    (18, '统计分析',     'stat',             'menu',   0,  '/stat',                 5, 1),
    (19, '数据分析',     'stat:analysis',    'page',   18, '/stat/analysis',        1, 1),
    -- 服务评价
    (20, '服务评价',     'evaluation',       'menu',   0,  '/evaluation',           6, 1),
    (21, '评价列表',     'evaluation:list',  'page',   20, '/evaluation/list',      1, 1),
    (22, '数据报表',     'report',           'page',   20, '/report',               2, 1),
    -- 按钮权限（不在权限树中，分配页面时自动包含）
    (23, '用户新增',     'user:add',         'button', 3,  NULL, 1, 1),
    (24, '用户修改',     'user:update',      'button', 3,  NULL, 2, 1),
    (25, '用户详情',     'user:detail',      'button', 3,  NULL, 3, 1),
    (26, '申请审核',     'apply:audit',      'button', 14, NULL, 1, 1),
    (27, '居民新增',     'resident:add',     'button', 8,  NULL, 1, 1),
    (28, '居民修改',     'resident:update',  'button', 8,  NULL, 2, 1),
    (29, '居民删除',     'resident:delete',  'button', 8,  NULL, 3, 1),
    (30, '居民详情',     'resident:detail',  'button', 8,  NULL, 4, 1),
    (31, '服务新增',     'item:add',         'button', 9,  NULL, 1, 1),
    (32, '服务修改',     'item:update',      'button', 9,  NULL, 2, 1),
    (33, '服务删除',     'item:delete',      'button', 9,  NULL, 3, 1),
    (34, '服务详情',     'item:detail',      'button', 9,  NULL, 4, 1),
    (35, '趋势分析',     'stat:trend',       'button', 19, NULL, 1, 1),
    (36, '分布统计',     'stat:distribution','button', 19, NULL, 2, 1);

-- -----------------------------------------------------
-- 角色权限分配
-- -----------------------------------------------------

-- 超级管理员：所有权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 1, id FROM `sys_permission`;

-- 社区受理员：仪表盘 + 服务管理 + 申请管理 + 评价
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
    (2, 1),   -- 仪表盘
    (2, 8),   -- 居民管理
    (2, 9),   -- 服务项目
    (2, 10),  -- 办事指南管理
    (2, 11),  -- 办事指南公开页
    (2, 12),  -- 附件管理
    (2, 14),  -- 申请列表
    (2, 15),  -- 超时预警
    (2, 16),  -- 预约管理
    (2, 17),  -- 消息通知
    (2, 21);  -- 评价列表

-- 街道审核员：仪表盘 + 服务管理 + 申请管理 + 统计分析
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
    (3, 1),   -- 仪表盘
    (3, 8),   -- 居民管理
    (3, 9),   -- 服务项目
    (3, 14),  -- 申请列表
    (3, 15),  -- 超时预警
    (3, 19);  -- 数据分析

-- -----------------------------------------------------
-- 默认管理员账号
-- 密码: admin123（盐值: salt123）
-- -----------------------------------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`) VALUES
    (1, 'admin', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '超级管理员', '13800138000', 'admin@community.com', 1);

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- -----------------------------------------------------
-- 示例服务项目
-- -----------------------------------------------------
INSERT INTO `service_item` (`id`, `item_name`, `item_code`, `category`, `description`, `fee`, `processing_days`, `required_docs`, `status`) VALUES
    (1, '居住证明办理', 'RESIDENCE_CERT',  '证件办理', '为本社区居民开具居住证明',         0.00,  3, '身份证原件、房屋租赁合同或房产证',       1),
    (2, '低保申请',     'LOW_INCOME_APPLY','救助帮扶', '最低生活保障金申请',               0.00,  7, '户口本、收入证明、残疾证（如有）',       1),
    (3, '老年优待证办理','ELDERLY_CARD',   '养老服务', '60岁以上老年人优待证办理',          0.00,  5, '身份证原件、近期免冠照片2张',             1),
    (4, '独生子女证办理','ONE_CHILD_CERT', '计划生育', '独生子女父母光荣证办理',            0.00,  5, '结婚证、户口本、独生子女证明',             1),
    (5, '就业登记',     'EMPLOYMENT_REG',  '就业服务', '失业人员就业登记',                 0.00,  3, '身份证、失业证、学历证明',                 1);

-- -----------------------------------------------------
-- 系统配置
-- -----------------------------------------------------
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`) VALUES
    ('system_name',             '社区便民服务申请与进度管理系统', '系统名称'),
    ('processing_deadline_days','7',                             '默认办理时限（天）'),
    ('notice_enabled',          'true',                          '是否启用办理时限提醒'),
    ('contact_phone',           '010-12345678',                  '系统联系电话'),
    ('org_name',                '街道便民服务中心',               '所属组织名称'),
    ('max_attachment_size',     '10MB',                          '附件最大上传大小'),
    ('allowed_attachment_types','jpg,png,pdf,doc,docx',          '允许上传的附件类型'),
    ('audit_reminder_hours',    '48',                            '审核超时提醒（小时）'),
    ('default_page_size',       '10',                            '默认分页大小'),
    ('logo_url',                '/uploads/system/logo.png',      '系统Logo地址');

-- =====================================================
-- 数据库初始化完成
-- 默认登录账号: admin / admin123
-- =====================================================
