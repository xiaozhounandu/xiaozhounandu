# 客户管理系统 V2.0 后端启动文档

## 📋 项目概述

基于 Spring Boot 3.0 + MyBatis + MySQL + Vue 3 的客户管理系统

**后端技术栈:**
- Spring Boot 3.0.0
- MyBatis 3.0.3
- MySQL 8.0.33
- Spring Security (BCrypt加密)
- Lombok

**前端技术栈:**
- Vue 3.5.25
- Vue Router 4.6.3
- Vite 7.2.4
- Axios

---

## 🚀 快速启动 (后端)

### 1. 准备数据库

```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source /Users/weizhijie/Desktop/xiaozhounandu-main/src/main/resources/sql/init-database.sql
```

或者直接在命令行执行:
```bash
mysql -u root -p < /Users/weizhijie/Desktop/xiaozhounandu-main/src/main/resources/sql/init-database.sql
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiaozhounandu_v2?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root  # 修改为你的MySQL用户名
    password: root  # 修改为你的MySQL密码
```

### 3. 编译和运行

```bash
# 进入项目目录
cd /Users/weizhijie/Desktop/xiaozhounandu-main

# 使用Maven运行
mvn spring-boot:run

# 或者先编译再运行
mvn clean package
java -jar target/xiaozhounandu-0.0.1-SNAPSHOT.jar
```

后端将在 `http://localhost:8080` 启动

---

## 🚀 快速启动 (前端)

### 1. 进入前端目录

```bash
cd /Users/weizhijie/Desktop/xiaozhounandu-main/v-xiaozhounandu/v-xiaozhounandu
```

### 2. 安装依赖

如果需要安装axios和pinia:
```bash
npm install axios pinia
```

### 3. 启动开发服务器

```bash
npm run dev
```

前端将在 `http://localhost:5173` 启动

---

## 📊 测试账号

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|----------|
| admin | admin123 | ADMIN | 系统管理员，所有权限 |
| manager | admin123 | MANAGER | 销售经理，客户管理 + 统计 |
| user | admin123 | USER | 普通用户，仅自己客户 |

---

## 🔌 API接口文档

### 认证模块 `/api/auth`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/auth/register` | POST | 注册 | 公开 |
| `/auth/login` | POST | 登录 | 公开 |
| `/auth/logout` | POST | 登出 | 已登录 |
| `/auth/current` | GET | 获取当前用户 | 已登录 |
| `/auth/password` | PUT | 修改密码 | 已登录 |

**登录示例:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 客户管理 `/api/customers`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/customers?page=&name=&company=&status=&level=` | GET | 分页查询 |
| `/customers/{id}` | GET | 客户详情 |
| `/customers` | POST | 新增客户 |
| `/customers/{id}` | PUT | 修改客户 |
| `/customers/{id}` | DELETE | 删除客户 |
| `/customers/{id}/transfer` | PUT | 转移客户 |

**查询示例:**
```bash
curl "http://localhost:8080/api/customers?page=1&pageSize=10" \
  -H "Authorization: Bearer <token>"
```

### 跟进记录 `/api/follow-ups`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/follow-ups/customer/{id}?page=&pageSize=` | GET | 客户跟进历史 |
| `/follow-ups` | POST | 添加跟进 |

### 统计分析 `/api/stats`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/stats/dashboard` | GET | 数据看板 | ADMIN/MANAGER |

### 日志管理 `/api/logs`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/logs/operations` | GET | 操作日志 | ADMIN |
| `/logs/login` | GET | 登录日志 | ADMIN |

---

## 📁 项目结构

```
src/main/java/com/xiaozhounandu/
├── XiaozhounanduApplication.java  # 启动类
├── config/
│   ├── WebConfig.java            # Web配置
│   ├── MyBatisConfig.java        # MyBatis配置
│   └── GlobalExceptionHandler.java  # 全局异常处理
├── interceptor/
│   └── AuthInterceptor.java      # 认证拦截器
├── controller/                   # 控制器层
│   ├── AuthController.java
│   ├── CustomerController.java
│   ├── FollowUpController.java
│   ├── LogController.java
│   └── StatsController.java
├── service/impl/                 # 业务逻辑层
│   ├── AuthServiceImpl.java
│   ├── CustomerServiceImpl.java
│   ├── FollowUpServiceImpl.java
│   ├── LogServiceImpl.java
│   └── UserServiceImpl.java
├── mapper/                       # 数据访问层
│   ├── UserMapper.java
│   ├── CustomerMapper.java
│   ├── FollowUpMapper.java
│   ├── OperationLogMapper.java
│   └── LoginLogMapper.java
├── entity/                       # 实体类
│   ├── User.java
│   ├── Customer.java
│   ├── FollowUp.java
│   ├── OperationLog.java
│   └── LoginLog.java
├── dto/                          # DTO对象
│   ├── request/                  # 请求对象
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CustomerRequest.java
│   │   └── ...
│   └── response/                 # 响应对象
│       ├── ApiResult.java
│       ├── UserInfoResponse.java
│       └── ...
└── util/                         # 工具类
    ├── EncryptionUtil.java       # 加密工具
    └── IpUtil.java               # IP工具

src/main/resources/
├── mapper/                       # MyBatis XML
│   ├── UserMapper.xml
│   ├── CustomerMapper.xml
│   ├── FollowUpMapper.xml
│   ├── OperationLogMapper.xml
│   └── LoginLogMapper.xml
├── sql/
│   └── init-database.sql         # 数据库初始化
└── application.yml               # 配置文件
```

---

## 💡 核心特性

### 1. 安全性
- ✅ BCrypt密码加密存储
- ✅ JWT Token认证（内存存储，可扩展为Redis）
- ✅ 拦截器权限控制
- ✅ 角色分级权限（ADMIN/MANAGER/USER）

### 2. 数据模型
- ✅ 用户管理（注册、登录、修改密码）
- ✅ 客户管理（CRUD + 软删除 + 归属人转移）
- ✅ 跟进记录（客户关联 + 类型分类）
- ✅ 操作日志（记录所有关键操作）
- ✅ 登录日志（记录登录历史）

### 3. API设计
- ✅ 统一响应格式（ApiResult）
- ✅ 分页查询支持
- ✅ RESTful接口规范
- ✅ 统一异常处理

### 4. 日志系统
- ✅ 操作日志（增删改查记录）
- ✅ 登录日志（成功/失败记录）
- ✅ 详细信息（IP、UserAgent、时间戳）

---

## ⚠️ 注意事项

### 数据库配置
- 默认连接: `localhost:3306/xiaozhounandu_v2`
- 请根据实际情况修改 `application.yml` 中的数据库连接信息

### 安全性
- 生产环境建议使用Redis存储Token
- 使用JWT替代简单的UUID Token
- 添加接口限流和防刷机制

### 性能优化
- 复杂查询建议添加索引
- 统计查询考虑使用缓存
- 日志记录考虑异步处理

---

## 🔄 后续改进计划

1. **安全性增强**
   - [ ] 使用JWT替代内存Token
   - [ ] 集成Redis存储Session
   - [ ] 添加接口限流
   - [ ] 密码强度校验

2. **功能增强**
   - [ ] 客户导入/导出
   - [ ] 数据可视化图表
   - [ ] 邮件通知功能
   - [ ] 数据备份功能

3. **代码优化**
   - [ ] 添加单元测试
   - [ ] 集成Swagger文档
   - [ ] AOP实现日志记录
   - [ ] 优化SQL性能

---

## 📞 联系方式

如有问题，请检查:
1. 数据库是否正确创建和初始化
2. application.yml中的数据库连接配置
3. Maven依赖是否下载完整
4. 端口8080和5173是否被占用

---

**最后更新**: 2025-12-18
