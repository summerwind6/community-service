-- =====================================================
-- 社区便民服务申请与进度管理系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- 创建时间: 2026-05-11
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS community_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE community_service;

-- -----------------------------------------------------
-- 1. 系统用户表 (sys_user)
-- 存储系统登录用户信息，包含3种角色：超级管理员、社区受理员、街道审核员
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 2. 角色表 (sys_role)
-- 定义系统角色：ADMIN-超级管理员、CLERK-社区受理员、REVIEWER-街道审核员
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 3. 权限表 (sys_permission)
-- 定义菜单权限和操作权限
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 4. 角色权限关联表 (sys_role_perm)
-- 角色与权限的多对多关系
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 5. 用户角色关联表 (sys_user_role)
-- 用户与角色的多对多关系
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 6. 居民信息表 (resident_info)
-- 存储社区居民基本信息
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 7. 服务项目表 (service_item)
-- 定义可申请的便民服务项目
-- -----------------------------------------------------
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

-- -----------------------------------------------------
-- 8. 申请单表 (application_form)
-- 存储居民提交的服务申请记录
-- -----------------------------------------------------
DROP TABLE IF EXISTS `application_form`;
CREATE TABLE `application_form` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `apply_no` VARCHAR(50) NOT NULL COMMENT '申请编号（唯一）',
    `resident_id` BIGINT DEFAULT NULL COMMENT '申请人ID（居民表）',
    `resident_name` VARCHAR(50) DEFAULT NULL COMMENT '申请人姓名',
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

-- -----------------------------------------------------
-- 9. 审核记录表 (audit_record)
-- 存储每条申请的审核历史记录
-- -----------------------------------------------------
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
-- 初始化默认数据
-- =====================================================

-- 插入默认角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`) VALUES
('超级管理员', 'ADMIN', '拥有系统所有权限', 1),
('社区受理员', 'CLERK', '负责受理居民的服务申请', 1),
('街道审核员', 'REVIEWER', '负责审核服务申请', 1);

-- 插入默认权限数据（菜单级权限）
INSERT INTO `sys_permission` (`perm_name`, `perm_code`, `perm_type`, `parent_id`, `url`, `sort_order`, `status`) VALUES
('仪表盘', 'stat:dashboard', 'menu', 0, '/dashboard', 1, 1),
('系统管理', 'system', 'menu', 0, '/system', 2, 1),
('用户管理', 'user:list', 'button', 2, null, 1, 1),
('用户新增', 'user:add', 'button', 2, null, 2, 1),
('用户修改', 'user:update', 'button', 2, null, 3, 1),
('用户详情', 'user:detail', 'button', 2, null, 4, 1),
('角色管理', 'role:list', 'button', 2, null, 5, 1),
('服务管理', 'service', 'menu', 0, '/service', 3, 1),
('服务列表', 'item:list', 'button', 3, null, 1, 1),
('服务新增', 'item:add', 'button', 3, null, 2, 1),
('服务修改', 'item:update', 'button', 3, null, 3, 1),
('服务删除', 'item:delete', 'button', 3, null, 4, 1),
('服务详情', 'item:detail', 'button', 3, null, 5, 1),
('申请管理', 'apply', 'menu', 0, '/apply', 4, 1),
('申请列表', 'apply:list', 'button', 4, null, 1, 1),
('申请审核', 'apply:audit', 'button', 4, null, 2, 1),
('统计分析', 'stat', 'menu', 0, '/stat', 5, 1),
('趋势分析', 'stat:trend', 'button', 5, null, 1, 1),
('分布统计', 'stat:distribution', 'button', 5, null, 2, 1);

-- 为超级管理员分配所有权限（ID=1的角色获取所有权限）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 1, id FROM `sys_permission`;

-- 为社区受理员分配部分权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
(2, 1),   -- 仪表盘
(2, 10),  -- 服务列表
(2, 15),  -- 申请列表
(2, 16),  -- 申请审核
(2, 18);  -- 统计分析

-- 为街道审核员分配部分权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
(3, 1),   -- 仪表盘
(3, 10),  -- 服务列表
(3, 15),  -- 申请列表
(3, 16),  -- 申请审核
(3, 18);  -- 统计分析

-- 插入默认超级管理员账号
-- 密码: admin123 (使用Shiro MD5加密，盐值随机)
INSERT INTO `sys_user` (`username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'salt123', '超级管理员', '13800138000', 'admin@community.com', 1);

-- 为管理员分配角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 插入示例服务项目数据
INSERT INTO `service_item` (`item_name`, `item_code`, `category`, `description`, `fee`, `processing_days`, `required_docs`, `status`) VALUES
('居住证明办理', 'RESIDENCE_CERT', '证件办理', '为本社区居民开具居住证明', 0.00, 3, '身份证原件、房屋租赁合同或房产证', 1),
('低保申请', 'LOW_INCOME_APPLY', '救助帮扶', '最低生活保障金申请', 0.00, 7, '户口本、收入证明、残疾证（如有）', 1),
('老年优待证办理', 'ELDERLY_CARD', '养老服务', '60岁以上老年人优待证办理', 0.00, 5, '身份证原件、近期免冠照片2张', 1),
('独生子女证办理', 'ONE_CHILD_CERT', '计划生育', '独生子女父母光荣证办理', 0.00, 5, '结婚证、户口本、独生子女证明', 1),
('就业登记', 'EMPLOYMENT_REG', '就业服务', '失业人员就业登记', 0.00, 3, '身份证、失业证、学历证明', 1);

-- =====================================================
-- 数据库初始化完成
-- 默认登录账号: admin / admin123
-- =====================================================
