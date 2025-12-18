-- ==================================================
-- 客户管理系统 V2.0 数据库初始化脚本
-- 数据库名: xiaozhounandu_v2
-- ==================================================

CREATE DATABASE IF NOT EXISTS `xiaozhounandu_v2`
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE `xiaozhounandu_v2`;

-- ==================================================
-- 1. 用户表 (user)
-- ==================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN/USER/MANAGER',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `uk_username` (`username`),
  INDEX `idx_role` (`role`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户 (密码: admin123 - BCrypt加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`) VALUES
('admin', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '系统管理员', 'admin@example.com', 'ADMIN', 1),
('manager', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '销售经理', 'manager@example.com', 'MANAGER', 1),
('user', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '普通用户', 'user@example.com', 'USER', 1);

-- ==================================================
-- 2. 客户表 (customer)
-- ==================================================
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '客户姓名',
  `company` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `industry` VARCHAR(50) DEFAULT NULL COMMENT '行业',
  `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '来源',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-删除, 1-正常, 2-成交, 3-流失',
  `level` VARCHAR(20) DEFAULT 'A' COMMENT '客户等级: A/B/C/D',
  `owner_id` BIGINT NOT NULL COMMENT '归属人ID',
  `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
  `remark` TEXT COMMENT '备注',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_owner_id` (`owner_id`),
  INDEX `idx_creator_id` (`creator_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_name` (`name`),
  INDEX `idx_company` (`company`),
  FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`creator_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 插入测试客户
INSERT INTO `customer` (`name`, `company`, `phone`, `email`, `industry`, `position`, `address`, `source`, `status`, `level`, `owner_id`, `creator_id`, `remark`, `next_follow_time`) VALUES
('张三丰', '武当科技有限公司', '13800138000', 'zhangsan@wudang.com', '互联网', 'CEO', '湖北省十堰市武当山', '线上', 1, 'A', 1, 1, '意向强烈，需要尽快跟进', '2025-12-25 10:00:00'),
('李四光', '地质勘探集团', '13900139000', 'lisi@geology.com', '能源', '部门经理', '北京市朝阳区', '推荐', 1, 'B', 2, 1, '对产品感兴趣，需技术演示', '2025-12-26 14:00:00'),
('王五', '未来科技', '13700137000', 'wangwu@future.com', '人工智能', 'CTO', '上海市浦东新区', '线下', 2, 'A', 3, 2, '已成交，持续服务中', NULL),
('赵六', '传统制造业', '13600136000', 'zhao@manufacture.com', '制造', '采购总监', '广东省深圳市', '其他', 3, 'C', 1, 1, '客户流失，竞品价格更低', NULL);

-- ==================================================
-- 3. 跟进记录表 (follow_up)
-- ==================================================
DROP TABLE IF EXISTS `follow_up`;
CREATE TABLE `follow_up` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '跟进ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `follower_id` BIGINT NOT NULL COMMENT '跟进人ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: CALL/EMAIL/MEETING/WECHAT/OTHER',
  `content` TEXT NOT NULL COMMENT '跟进内容',
  `result` TEXT COMMENT '跟进结果',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `attachment` VARCHAR(255) DEFAULT NULL COMMENT '附件URL',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_follower_id` (`follower_id`),
  INDEX `idx_type` (`type`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`follower_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跟进记录表';

-- 插入测试跟进
INSERT INTO `follow_up` (`customer_id`, `follower_id`, `type`, `content`, `result`, `next_follow_time`) VALUES
(1, 1, 'CALL', '电话沟通产品需求', '客户表示很有兴趣，约定下周拜访', '2025-12-25 10:00:00'),
(1, 1, 'MEETING', '上门拜访，产品演示', '技术方案认可，进入商务谈判阶段', '2025-12-28 14:00:00'),
(2, 2, 'EMAIL', '发送产品报价单', '等待客户回复', '2025-12-26 09:00:00');

-- ==================================================
-- 4. 操作日志表 (operation_log)
-- ==================================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
  `user_id` BIGINT NOT NULL COMMENT '操作人ID',
  `module` VARCHAR(50) NOT NULL COMMENT '模块名称',
  `operation` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `target_id` BIGINT DEFAULT NULL COMMENT '目标ID',
  `target_name` VARCHAR(100) DEFAULT NULL COMMENT '目标名称',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` TEXT COMMENT 'UserAgent',
  `before_data` TEXT COMMENT '变更前数据(JSON)',
  `after_data` TEXT COMMENT '变更后数据(JSON)',
  `result` TINYINT NOT NULL DEFAULT 1 COMMENT '结果: 0-失败, 1-成功',
  `error_msg` TEXT COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_module` (`module`),
  INDEX `idx_operation` (`operation`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ==================================================
-- 5. 登录日志表 (login_log)
-- ==================================================
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '登录日志ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
  `device` VARCHAR(100) DEFAULT NULL COMMENT '设备信息',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '登录地点',
  `result` TINYINT NOT NULL DEFAULT 1 COMMENT '结果: 0-失败, 1-成功',
  `error_msg` VARCHAR(255) DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_username` (`username`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ==================================================
-- 验证数据
-- ==================================================
SELECT '用户表数据:' AS '';
SELECT id, username, nickname, role, status FROM user;

SELECT '客户表数据:' AS '';
SELECT id, name, company, status, level, owner_id FROM customer;

SELECT '跟进记录数据:' AS '';
SELECT id, customer_id, type, content FROM follow_up;

-- ==================================================
-- 密码说明
-- ==================================================
-- 测试账号密码均为: admin123
-- 采用BCrypt加密方式
-- 用户名: admin (管理员), manager (销售经理), user (普通用户)
