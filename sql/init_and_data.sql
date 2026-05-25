/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80022 (8.0.22)
 Source Host           : localhost:3306
 Source Schema         : community_service

 Target Server Type    : MySQL
 Target Server Version : 80022 (8.0.22)
 File Encoding         : 65001

 Date: 25/05/2026 16:58:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for application_form
-- ----------------------------
DROP TABLE IF EXISTS `application_form`;
CREATE TABLE `application_form`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请编号（唯一）',
  `resident_id` bigint NULL DEFAULT NULL COMMENT '申请人ID（居民表）',
  `resident_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请人姓名',
  `service_item_id` bigint NOT NULL COMMENT '服务项目ID',
  `service_item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务项目名称',
  `apply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请内容描述',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附件路径（JSON数组）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待受理/PROCESSING处理中/APPROVED通过/REJECTED驳回/COMPLETED已完成',
  `fee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实际费用',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '当前处理人ID',
  `handler_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前处理人姓名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_apply_no`(`apply_no` ASC) USING BTREE,
  INDEX `idx_resident_id`(`resident_id` ASC) USING BTREE,
  INDEX `idx_service_item_id`(`service_item_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_handler_id`(`handler_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '申请单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of application_form
-- ----------------------------
INSERT INTO `application_form` VALUES (1, 'SQ20260511001', 1, '李明华', 1, '居住证明办理', '因工作调动需要开具居住证明', '[\"/uploads/files/id_card_1.jpg\",\"/uploads/files/contract_1.pdf\"]', 'APPROVED', 0.00, 6, '张三', '材料齐全，已办结', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (2, 'SQ20260511002', 2, '王秀英', 3, '老年优待证办理', '申请办理老年优待证，已满60周岁', '[\"/uploads/files/id_card_2.jpg\",\"/uploads/files/photo_2.jpg\"]', 'APPROVED', 0.00, 6, '张三', '已审批通过，待领取证件', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (3, 'SQ20260511003', 3, '张伟', 5, '就业登记', '大学毕业后未就业，需要办理就业登记', '[\"/uploads/files/id_card_3.jpg\",\"/uploads/files/diploma_3.pdf\"]', 'COMPLETED', 0.00, 6, '张三', '', '2026-05-18 14:19:24', '2026-05-18 14:27:49', 0);
INSERT INTO `application_form` VALUES (4, 'SQ20260511004', 4, '刘美丽', 2, '低保申请', '家庭收入较低，申请最低生活保障', '[\"/uploads/files/hukou_4.pdf\",\"/uploads/files/income_4.pdf\"]', 'PROCESSING', 0.00, 7, '李四', '', '2026-05-18 14:19:24', '2026-05-18 14:28:13', 0);
INSERT INTO `application_form` VALUES (5, 'SQ20260511005', 5, '陈强', 1, '居住证明办理', '孩子入学需要居住证明', '[\"/uploads/files/id_card_5.jpg\"]', 'APPROVED', 0.00, 6, '张三', '已办结，证明已发放', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (6, 'SQ20260511006', 6, '赵敏', 4, '独生子女证办理', '独生子女证遗失需要补办', '[\"/uploads/files/marriage_cert_6.pdf\",\"/uploads/files/hukou_6.pdf\"]', 'REJECTED', 0.00, 7, '李四', '材料不齐全，缺少独生子女证明', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (7, 'SQ20260511007', 7, '孙志强', 3, '老年优待证办理', '申请老年优待证享受公交优惠', '[\"/uploads/files/id_card_7.jpg\",\"/uploads/files/photo_7.jpg\"]', 'PROCESSING', 0.00, 6, '张三', '照片规格不符，已通知重新提交', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (8, 'SQ20260511008', 8, '周晓红', 5, '就业登记', '离职后需要重新办理就业登记', '[\"/uploads/files/id_card_8.jpg\",\"/uploads/files/resignation_8.pdf\"]', 'PROCESSING', 0.00, 7, '李四', '', '2026-05-18 14:19:24', '2026-05-18 15:11:45', 0);
INSERT INTO `application_form` VALUES (9, 'SQ20260511009', 9, '吴建国', 1, '居住证明办理', '办理房屋过户需要居住证明', '[\"/uploads/files/id_card_9.jpg\",\"/uploads/files/property_9.pdf\"]', 'APPROVED', 0.00, 6, '张三', '证明已开具并发放', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (10, 'SQ20260511010', 10, '郑秀兰', 2, '低保申请', '因病致贫，申请最低生活保障', '[\"/uploads/files/hukou_10.pdf\",\"/uploads/files/medical_10.pdf\"]', 'PROCESSING', 0.00, 8, '王五', '需进一步核实医疗费用情况', '2026-05-18 14:19:24', '2026-05-18 14:19:24', 0);
INSERT INTO `application_form` VALUES (11, 'AP202605180001', 2, '王秀英', 4, '独生子女证办理', 'ceshi ', NULL, 'REJECTED', 0.00, 7, '李四', 'ceshi', '2026-05-18 15:18:42', '2026-05-18 15:19:31', 0);
INSERT INTO `application_form` VALUES (19, 'AP202605180002', 1, '李明华', 1, '居住证明办理', '123', NULL, 'PROCESSING', 0.00, 1, '超级管理员', '社区受理员受理', '2026-05-18 17:28:09', '2026-05-18 17:28:09', 0);

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resident_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预约人姓名',
  `resident_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预约人电话',
  `service_item_id` bigint NULL DEFAULT NULL COMMENT '预约服务事项ID',
  `service_item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务事项名称',
  `appointment_date` date NULL DEFAULT NULL COMMENT '预约日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '时间段：AM上午/PM下午',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待确认/CONFIRMED已确认/CANCELED已取消/COMPLETED已完成',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_appointment_date`(`appointment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, '张三', '12345678', 5, '就业登记', '2026-05-27', 'AM', 'PENDING', '', '2026-05-25 16:08:51');

-- ----------------------------
-- Table structure for audit_record
-- ----------------------------
DROP TABLE IF EXISTS `audit_record`;
CREATE TABLE `audit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` bigint NOT NULL COMMENT '申请单ID',
  `audit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核类型：COMMUNITY社区审核/STREET街道审核',
  `auditor_id` bigint NOT NULL COMMENT '审核人ID',
  `auditor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `audit_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核结果：APPROVED通过/REJECTED驳回/PENDING待审核',
  `audit_opinion` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_auditor_id`(`auditor_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_record
-- ----------------------------
INSERT INTO `audit_record` VALUES (1, 1, 'COMMUNITY', 6, '张三', 'APPROVED', '材料齐全，符合办理条件，同意开具证明', '2026-05-11 09:30:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (2, 1, 'STREET', 7, '李四', 'APPROVED', '审核通过，同意办理', '2026-05-11 10:15:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (3, 2, 'COMMUNITY', 6, '张三', 'APPROVED', '年龄符合要求，材料真实有效', '2026-05-11 09:45:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (4, 2, 'STREET', 7, '李四', 'APPROVED', '审核通过，准予办理', '2026-05-11 10:30:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (5, 3, 'COMMUNITY', 6, '张三', 'PENDING', '正在审核学历证明真实性', '2026-05-11 11:00:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (6, 5, 'COMMUNITY', 6, '张三', 'APPROVED', '情况属实，同意开具居住证明', '2026-05-11 08:30:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (7, 5, 'STREET', 8, '王五', 'APPROVED', '审核通过', '2026-05-11 09:00:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (8, 6, 'COMMUNITY', 6, '张三', 'REJECTED', '材料不完整，缺少独生子女证明原件', '2026-05-11 10:00:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (9, 7, 'COMMUNITY', 6, '张三', 'PENDING', '照片不符合规范，需重新提交', '2026-05-11 13:00:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (10, 9, 'COMMUNITY', 6, '张三', 'APPROVED', '材料齐全，同意办理', '2026-05-11 14:00:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (11, 9, 'STREET', 7, '李四', 'APPROVED', '审核通过', '2026-05-11 14:30:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (12, 10, 'COMMUNITY', 6, '张三', 'PENDING', '医疗费用较高，转街道审核员复审', '2026-05-11 15:00:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (13, 10, 'STREET', 8, '王五', 'PENDING', '已接收复审，需核实医疗费用明细', '2026-05-11 15:30:00', '2026-05-18 14:19:49');
INSERT INTO `audit_record` VALUES (14, 3, 'STREET', 6, '张三', 'APPROVED', '', '2026-05-18 14:27:49', '2026-05-18 14:27:49');
INSERT INTO `audit_record` VALUES (15, 4, 'COMMUNITY', 7, '李四', 'APPROVED', '', '2026-05-18 14:28:13', '2026-05-18 14:28:13');
INSERT INTO `audit_record` VALUES (16, 8, 'COMMUNITY', 7, '李四', 'APPROVED', '', '2026-05-18 15:11:45', '2026-05-18 15:11:45');
INSERT INTO `audit_record` VALUES (17, 11, 'COMMUNITY', 6, '张三', 'APPROVED', '社区受理员受理', '2026-05-18 15:18:43', '2026-05-18 15:18:43');
INSERT INTO `audit_record` VALUES (18, 11, 'STREET', 7, '李四', 'REJECTED', 'ceshi', '2026-05-18 15:19:31', '2026-05-18 15:19:31');
INSERT INTO `audit_record` VALUES (19, 19, 'COMMUNITY', 1, '超级管理员', 'APPROVED', '社区受理员受理', '2026-05-18 17:28:09', '2026-05-18 17:28:09');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SYSTEM' COMMENT '类型：AUDIT审核通知/SYSTEM系统通知',
  `target_id` bigint NULL DEFAULT NULL COMMENT '关联目标ID（如申请单ID）',
  `target_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收人姓名',
  `target_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收人电话',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0未读/1已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, '测试', '测试', 'SYSTEM', NULL, '', '', 1, '2026-05-25 16:09:44');

-- ----------------------------
-- Table structure for resident_info
-- ----------------------------
DROP TABLE IF EXISTS `resident_info`;
CREATE TABLE `resident_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `community` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属社区',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别：M-男 F-女',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id_card`(`id_card` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '居民信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resident_info
-- ----------------------------
INSERT INTO `resident_info` VALUES (1, '李明华', '110101198503150012', '13900139001', '朝阳区建国路100号1号楼2单元301', '建国路社区', 'M', '1985-03-15', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (2, '王秀英', '110101196008200023', '13900139002', '朝阳区建国路100号2号楼1单元502', '建国路社区', 'F', '1960-08-20', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (3, '张伟', '110101199007250034', '13900139003', '朝阳区光华路50号3号楼3单元102', '光华路社区', 'M', '1990-07-25', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (4, '刘美丽', '110101197512100045', '13900139004', '朝阳区光华路50号1号楼4单元201', '光华路社区', 'F', '1975-12-10', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (5, '陈强', '110101198802280056', '13900139005', '朝阳区和平街20号5号楼2单元401', '和平街社区', 'M', '1988-02-28', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (6, '赵敏', '110101199209050067', '13900139006', '朝阳区和平街20号3号楼1单元302', '和平街社区', 'F', '1992-09-05', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (7, '孙志强', '110101195507180078', '13900139007', '朝阳区安定路30号2号楼3单元501', '安定路社区', 'M', '1955-07-18', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (8, '周晓红', '110101198210220089', '13900139008', '朝阳区安定路30号1号楼2单元102', '安定路社区', 'F', '1982-10-22', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (9, '吴建国', '110101197803090090', '13900139009', '朝阳区东大桥路15号4号楼1单元601', '东大桥社区', 'M', '1978-03-09', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);
INSERT INTO `resident_info` VALUES (10, '郑秀兰', '110101196512250101', '13900139010', '朝阳区东大桥路15号2号楼3单元302', '东大桥社区', 'F', '1965-12-25', '2026-05-18 14:18:58', '2026-05-18 14:18:58', 0);

-- ----------------------------
-- Table structure for service_item
-- ----------------------------
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务项目名称',
  `item_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目编码（唯一）',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务类别',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `fee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '费用（元）',
  `processing_days` int NULL DEFAULT 7 COMMENT '预计处理天数',
  `required_docs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '所需材料说明',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_item_code`(`item_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_item
-- ----------------------------
INSERT INTO `service_item` VALUES (1, '居住证明办理', 'RESIDENCE_CERT', '证件办理', '为本社区居民开具居住证明', 0.00, 3, '身份证原件、房屋租赁合同或房产证', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `service_item` VALUES (2, '低保申请', 'LOW_INCOME_APPLY', '救助帮扶', '最低生活保障金申请', 0.00, 7, '户口本、收入证明、残疾证（如有）', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `service_item` VALUES (3, '老年优待证办理', 'ELDERLY_CARD', '养老服务', '60岁以上老年人优待证办理', 0.00, 5, '身份证原件、近期免冠照片2张', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `service_item` VALUES (4, '独生子女证办理', 'ONE_CHILD_CERT', '计划生育', '独生子女父母光荣证办理', 0.00, 5, '结婚证、户口本、独生子女证明', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `service_item` VALUES (5, '就业登记', 'EMPLOYMENT_REG', '就业服务', '失业人员就业登记', 0.00, 3, '身份证、失业证、学历证明', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `service_item` VALUES (6, '残疾证办理', 'DISABILITY_CERT', '救助帮扶', '残疾人证件办理服务', 0.00, 10, '身份证、户口本、医院诊断证明、2寸照片3张', 1, '2026-05-18 14:19:58', '2026-05-18 14:19:58', 0);
INSERT INTO `service_item` VALUES (7, '生育服务登记', 'BIRTH_REG', '计划生育', '生育服务登记办理', 0.00, 5, '结婚证、户口本、身份证、B超单', 1, '2026-05-18 14:19:58', '2026-05-18 14:19:58', 0);
INSERT INTO `service_item` VALUES (8, '房屋租赁备案', 'HOUSE_RENTAL', '住房服务', '房屋租赁合同备案登记', 50.00, 3, '房产证、租赁合同、双方身份证', 1, '2026-05-18 14:19:58', '2026-05-18 14:19:58', 0);
INSERT INTO `service_item` VALUES (9, '暂住证办理', 'TEMP_RESIDENCE', '证件办理', '外来人口暂住证办理', 10.00, 7, '身份证、租房合同、房东房产证复印件', 1, '2026-05-18 14:19:58', '2026-05-18 14:19:58', 0);
INSERT INTO `service_item` VALUES (10, '社保卡申领', 'SOCIAL_SECURITY', '社保服务', '社会保险卡首次申领', 20.00, 15, '身份证、户口本、1寸照片2张', 1, '2026-05-18 14:19:58', '2026-05-18 14:19:58', 0);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键（唯一）',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置说明',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system_name', '社区便民服务申请与进度管理系统', '系统名称', '2026-05-13 10:03:05', '2026-05-13 10:03:05');
INSERT INTO `sys_config` VALUES (2, 'processing_deadline_days', '7', '默认办理时限（天）', '2026-05-13 10:03:05', '2026-05-13 10:03:05');
INSERT INTO `sys_config` VALUES (3, 'notice_enabled', 'true', '是否启用办理时限提醒', '2026-05-13 10:03:05', '2026-05-13 10:03:05');
INSERT INTO `sys_config` VALUES (4, 'contact_phone', '010-12345678', '系统联系电话', '2026-05-13 10:03:05', '2026-05-13 10:03:05');
INSERT INTO `sys_config` VALUES (5, 'org_name', '街道便民服务中心', '所属组织名称', '2026-05-13 10:03:05', '2026-05-13 10:03:05');
INSERT INTO `sys_config` VALUES (6, 'max_attachment_size', '10MB', '附件最大上传大小', '2026-05-18 14:20:09', '2026-05-18 14:20:09');
INSERT INTO `sys_config` VALUES (7, 'allowed_attachment_types', 'jpg,png,pdf,doc,docx', '允许上传的附件类型', '2026-05-18 14:20:09', '2026-05-18 14:20:09');
INSERT INTO `sys_config` VALUES (8, 'audit_reminder_hours', '48', '审核超时提醒（小时）', '2026-05-18 14:20:09', '2026-05-18 14:20:09');
INSERT INTO `sys_config` VALUES (9, 'default_page_size', '10', '默认分页大小', '2026-05-18 14:20:09', '2026-05-18 14:20:09');
INSERT INTO `sys_config` VALUES (10, 'logo_url', '/uploads/system/logo.png', '系统Logo地址', '2026-05-18 14:20:09', '2026-05-18 14:20:09');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `perm_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `perm_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限编码（唯一）',
  `perm_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'menu' COMMENT '权限类型：menu-菜单 button-按钮',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID（顶级为0）',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_perm_code`(`perm_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '仪表盘', 'stat:dashboard', 'menu', 0, '/dashboard', NULL, 1, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (2, '系统管理', 'system', 'menu', 0, '/system', NULL, 2, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (3, '用户管理', 'user:list', 'button', 2, NULL, NULL, 1, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (4, '用户新增', 'user:add', 'button', 2, NULL, NULL, 2, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (5, '用户修改', 'user:update', 'button', 2, NULL, NULL, 3, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (6, '用户详情', 'user:detail', 'button', 2, NULL, NULL, 4, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (7, '角色管理', 'role:list', 'button', 2, NULL, NULL, 5, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (8, '服务管理', 'service', 'menu', 0, '/service', NULL, 3, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (9, '服务列表', 'item:list', 'button', 3, NULL, NULL, 1, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (10, '服务新增', 'item:add', 'button', 3, NULL, NULL, 2, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (11, '服务修改', 'item:update', 'button', 3, NULL, NULL, 3, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (12, '服务删除', 'item:delete', 'button', 3, NULL, NULL, 4, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (13, '服务详情', 'item:detail', 'button', 3, NULL, NULL, 5, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (14, '申请管理', 'apply', 'menu', 0, '/apply', NULL, 4, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (15, '申请列表', 'apply:list', 'button', 4, NULL, NULL, 1, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (16, '申请审核', 'apply:audit', 'button', 4, NULL, NULL, 2, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (17, '统计分析', 'stat', 'menu', 0, '/stat', NULL, 5, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (18, '趋势分析', 'stat:trend', 'button', 5, NULL, NULL, 1, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (19, '分布统计', 'stat:distribution', 'button', 5, NULL, NULL, 2, 1, '2026-05-12 10:22:17', 0);
INSERT INTO `sys_permission` VALUES (20, '预约管理', 'appointment', 'menu', 0, '/appointment', NULL, 6, 1, '2026-05-25 16:53:49', 0);
INSERT INTO `sys_permission` VALUES (21, '预约列表', 'appointment:list', 'button', 20, NULL, NULL, 1, 1, '2026-05-25 16:53:49', 0);
INSERT INTO `sys_permission` VALUES (22, '预约管理', 'appointment:manage', 'button', 20, NULL, NULL, 2, 1, '2026-05-25 16:53:49', 0);
INSERT INTO `sys_permission` VALUES (23, '消息通知', 'notification', 'menu', 0, '/notification', NULL, 7, 1, '2026-05-25 16:53:49', 0);
INSERT INTO `sys_permission` VALUES (24, '通知列表', 'notification:list', 'button', 23, NULL, NULL, 1, 1, '2026-05-25 16:53:49', 0);
INSERT INTO `sys_permission` VALUES (25, '发送通知', 'notification:send', 'button', 23, NULL, NULL, 2, 1, '2026-05-25 16:53:49', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码（唯一）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ADMIN', '拥有系统所有权限', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `sys_role` VALUES (2, '社区受理员', 'CLERK', '负责受理居民的服务申请', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);
INSERT INTO `sys_role` VALUES (3, '街道审核员', 'REVIEWER', '负责审核服务申请', 1, '2026-05-12 10:22:17', '2026-05-12 10:22:17', 0);

-- ----------------------------
-- Table structure for sys_role_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_perm`;
CREATE TABLE `sys_role_perm`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `perm_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id` ASC, `perm_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_perm_id`(`perm_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_perm
-- ----------------------------
INSERT INTO `sys_role_perm` VALUES (12, 1, 1);
INSERT INTO `sys_role_perm` VALUES (15, 1, 2);
INSERT INTO `sys_role_perm` VALUES (18, 1, 3);
INSERT INTO `sys_role_perm` VALUES (16, 1, 4);
INSERT INTO `sys_role_perm` VALUES (19, 1, 5);
INSERT INTO `sys_role_perm` VALUES (17, 1, 6);
INSERT INTO `sys_role_perm` VALUES (9, 1, 7);
INSERT INTO `sys_role_perm` VALUES (10, 1, 8);
INSERT INTO `sys_role_perm` VALUES (7, 1, 9);
INSERT INTO `sys_role_perm` VALUES (4, 1, 10);
INSERT INTO `sys_role_perm` VALUES (8, 1, 11);
INSERT INTO `sys_role_perm` VALUES (5, 1, 12);
INSERT INTO `sys_role_perm` VALUES (6, 1, 13);
INSERT INTO `sys_role_perm` VALUES (1, 1, 14);
INSERT INTO `sys_role_perm` VALUES (3, 1, 15);
INSERT INTO `sys_role_perm` VALUES (2, 1, 16);
INSERT INTO `sys_role_perm` VALUES (11, 1, 17);
INSERT INTO `sys_role_perm` VALUES (14, 1, 18);
INSERT INTO `sys_role_perm` VALUES (13, 1, 19);
INSERT INTO `sys_role_perm` VALUES (32, 2, 1);
INSERT INTO `sys_role_perm` VALUES (33, 2, 10);
INSERT INTO `sys_role_perm` VALUES (34, 2, 15);
INSERT INTO `sys_role_perm` VALUES (35, 2, 16);
INSERT INTO `sys_role_perm` VALUES (36, 2, 18);
INSERT INTO `sys_role_perm` VALUES (42, 2, 20);
INSERT INTO `sys_role_perm` VALUES (43, 2, 21);
INSERT INTO `sys_role_perm` VALUES (44, 2, 22);
INSERT INTO `sys_role_perm` VALUES (45, 2, 23);
INSERT INTO `sys_role_perm` VALUES (46, 2, 24);
INSERT INTO `sys_role_perm` VALUES (47, 2, 25);
INSERT INTO `sys_role_perm` VALUES (37, 3, 1);
INSERT INTO `sys_role_perm` VALUES (38, 3, 10);
INSERT INTO `sys_role_perm` VALUES (39, 3, 15);
INSERT INTO `sys_role_perm` VALUES (40, 3, 16);
INSERT INTO `sys_role_perm` VALUES (41, 3, 18);
INSERT INTO `sys_role_perm` VALUES (49, 3, 20);
INSERT INTO `sys_role_perm` VALUES (50, 3, 21);
INSERT INTO `sys_role_perm` VALUES (51, 3, 22);
INSERT INTO `sys_role_perm` VALUES (52, 3, 23);
INSERT INTO `sys_role_perm` VALUES (53, 3, 24);
INSERT INTO `sys_role_perm` VALUES (54, 3, 25);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（MD5加密）',
  `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码盐值',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '超级管理员', '13800138000', 'admin@community.com', NULL, 1, '2026-05-12 11:50:10', '2026-05-13 09:45:35', 0);
INSERT INTO `sys_user` VALUES (2, 'songziyu', '4ce9aaa77da7960143ea1a2bcf89dbdc', 'dc83d0291f724f099d7d7ce9623c9a7b', '宋梓瑜', '18505609978', '', NULL, 1, '2026-05-13 09:23:38', '2026-05-18 14:24:18', 1);
INSERT INTO `sys_user` VALUES (3, 'lianghaoyan', '60cd00770f4bf300fb9e3b92e0a9a0d6', '29b513117d88473f8f73f259dabd8d51', '梁浩琰', '', '', NULL, 1, '2026-05-13 09:24:55', '2026-05-18 14:24:15', 1);
INSERT INTO `sys_user` VALUES (4, '123', '5bd431a9ffe13361e62eafc6bb2f2c27', 'a93ce465cb0d4555af24c15f555f7aef', '123', '', '', NULL, 1, '2026-05-13 09:26:11', '2026-05-18 14:24:12', 1);
INSERT INTO `sys_user` VALUES (6, 'zhangsan', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '张三', '13800138001', 'zhangsan@community.com', NULL, 1, '2026-05-18 14:18:10', '2026-05-18 14:18:10', 0);
INSERT INTO `sys_user` VALUES (7, 'lisi', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '李四', '13800138002', 'lisi@community.com', NULL, 1, '2026-05-18 14:18:10', '2026-05-18 14:18:10', 0);
INSERT INTO `sys_user` VALUES (8, 'wangwu', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '王五', '13800138003', 'wangwu@community.com', NULL, 1, '2026-05-18 14:18:10', '2026-05-18 14:18:10', 0);
INSERT INTO `sys_user` VALUES (9, 'zhaoliu', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '赵六', '13800138004', 'zhaoliu@community.com', NULL, 1, '2026-05-18 14:18:10', '2026-05-18 14:18:10', 0);
INSERT INTO `sys_user` VALUES (10, 'sunqi', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '孙七', '13800138005', 'sunqi@community.com', NULL, 1, '2026-05-18 14:18:10', '2026-05-18 14:18:10', 0);
INSERT INTO `sys_user` VALUES (11, 'zhouba', '82d1d187ddcfbf1ba740510f28308c26', 'salt123', '周八', '13800138006', 'zhouba@community.com', NULL, 1, '2026-05-18 14:18:10', '2026-05-18 14:18:10', 0);
INSERT INTO `sys_user` VALUES (12, 'test1', '9166f765b6110b93f2fd015bbd952869', 'b49c4d6cfaba41cdbe6063888c707d65', '审核', '', '', NULL, 1, '2026-05-25 14:15:13', '2026-05-25 14:15:13', 0);
INSERT INTO `sys_user` VALUES (13, 'test2', '1ad59acff90873a3c2fc6bc9a9742fd4', '83063d1e09144c19b9649e1a0e0a12a7', '受理', '', '', NULL, 1, '2026-05-25 14:15:33', '2026-05-25 14:15:33', 0);
INSERT INTO `sys_user` VALUES (22, 'test3', '5dc384554f20db8c83ba4d097b74777c', 'e3aeb7c4b6de45629fc83e153b10798a', '受理3', '', '', NULL, 1, '2026-05-25 15:17:50', '2026-05-25 15:17:50', 0);
INSERT INTO `sys_user` VALUES (23, 'test4', '488c0dd5b521d2fa9f880f6fa6840459', 'e4fdcfaaa08d4036ae7647a26fd6989f', '审核4', '', '', NULL, 1, '2026-05-25 15:19:11', '2026-05-25 15:19:11', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 6, 2);
INSERT INTO `sys_user_role` VALUES (3, 7, 3);
INSERT INTO `sys_user_role` VALUES (4, 8, 3);
INSERT INTO `sys_user_role` VALUES (5, 22, 2);
INSERT INTO `sys_user_role` VALUES (6, 23, 3);

SET FOREIGN_KEY_CHECKS = 1;
