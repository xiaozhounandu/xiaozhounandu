-- 请在MySQL中执行以下SQL脚本来创建用户表

-- 1. 使用数据库
USE xiaozhounandu;

-- 2. 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 插入测试数据（如果不存在）
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `role`, `status`)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', 'ADMIN', 1);

INSERT IGNORE INTO `user` (`username`, `password`, `email`, `role`, `status`)
VALUES ('user', '482c811da5d5b4bc6d497ffa98491e38', 'user@example.com', 'USER', 1);

-- 4. 验证插入的数据
SELECT * FROM `user`;