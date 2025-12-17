# 登录模块问题修复指南

## 🔍 已发现的问题和解决方案

### 1. 数据库表创建问题
**问题**: 用户表 `user` 可能不存在
**解决方案**:
1. 执行 `db-setup.sql` 脚本
2. 或直接在MySQL中执行以下SQL:

```sql
USE xiaozhounandu;

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

INSERT IGNORE INTO `user` (`username`, `password`, `email`, `role`, `status`)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', 'ADMIN', 1);

INSERT IGNORE INTO `user` (`username`, `password`, `email`, `role`, `status`)
VALUES ('user', '482c811da5d5b4bc6d497ffa98491e38', 'user@example.com', 'USER', 1);
```

### 2. 测试后端连接
**测试API**: `GET http://localhost:8080/api/test/db`

```bash
curl http://localhost:8080/api/test/db
```

预期响应:
```json
{
  "success": true,
  "message": "数据库连接正常",
  "adminUser": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

### 3. 测试登录功能
**API**: `POST http://localhost:8080/api/auth/login`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

预期响应:
```json
{
  "success": true,
  "message": "登录成功",
  "token": "uuid-token-string",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

## 🚀 完整部署步骤

### 步骤1: 准备数据库
1. 连接到MySQL数据库
2. 确保数据库 `xiaozhounandu` 存在
3. 执行上面的SQL脚本创建用户表和测试数据

### 步骤2: 启动后端
```bash
cd /Users/weizhijie/Desktop/java/xiaozhounandu
mvn spring-boot:run
```
后端将在 `http://localhost:8080` 启动

### 步骤3: 启动前端
```bash
cd /Users/weizhijie/Desktop/java/xiaozhounandu/v-xiaozhounandu/v-xiaozhounandu
npm run dev
```
前端将在 `http://localhost:5173` 启动

### 步骤4: 测试登录
1. 访问 `http://localhost:5173`
2. 应该自动跳转到登录页面 `http://localhost:5173/login`
3. 使用测试账号登录:
   - 管理员: admin / admin123
   - 普通用户: user / user123

## 🔧 调试技巧

### 1. 检查后端日志
后端启动时会显示详细日志，注意查看:
- 数据库连接信息
- MyBatis映射器扫描情况
- 端口绑定信息

### 2. 浏览器开发者工具
- 检查Network标签页，查看API请求是否成功
- 检查Console标签页，查看JavaScript错误
- 检查Application标签页，查看localStorage中的token

### 3. 常见错误排查

**401 Unauthorized 错误**:
- 检查token是否存在
- 检查Authorization header格式是否正确
- 检查后端拦截器配置

**404 Not Found 错误**:
- 检查后端是否正常启动
- 检查API路径是否正确
- 检查Vite代理配置

**CORS 错误**:
- 检查后端CORS配置
- 检查前端API请求URL

## 📝 测试账号信息

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 管理员账号，拥有所有权限 |
| user | user123 | USER | 普通用户账号，权限受限 |

## 🔄 下一步改进建议

1. **密码加密升级**: 使用BCrypt替代MD5
2. **JWT实现**: 使用JWT替代简单UUID token
3. **Redis集成**: 使用Redis存储session信息
4. **权限细化**: 按功能模块控制权限
5. **日志记录**: 添加登录日志和操作日志