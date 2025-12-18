# 客户管理系统 V2.0 - 后端开发文档

> 基于 Spring Boot 3.0 + MyBatis + MySQL

---

## 📋 目录
1. [技术栈](#技术栈)
2. [包结构](#包结构)
3. [数据库设计](#数据库设计)
4. [API接口](#api接口)
5. [权限设计](#权限设计)
6. [开发步骤](#开发步骤)

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.0.0 | 核心框架 |
| MyBatis | 3.0.3 | ORM框架 |
| MySQL | 8.0.33 | 数据库 |
| Lombok | - | 简化代码 |
| BCrypt | - | 密码加密 |
| JWT | - | Token认证 |

---

## 包结构

```
com.xiaozhounandu
├── XiaozhounanduApplication.java  # 启动类
├── config/
│   ├── WebConfig.java            # Web配置
│   └── MyBatisConfig.java        # MyBatis配置
├── interceptor/
│   └── AuthInterceptor.java      # 认证拦截器
├── controller/
│   ├── AuthController.java       # 认证
│   ├── CustomerController.java   # 客户
│   ├── FollowUpController.java   # 跟进
│   ├── StatsController.java      # 统计
│   └── LogController.java        # 日志
├── service/impl/                 # 业务实现
├── mapper/                       # 数据访问
├── entity/                       # 实体类
├── dto/                          # DTO对象
└── util/                         # 工具类
```

---

## 数据库设计

### 表关系
```
用户表(user)
  ↓ 1:N
客户表(customer)
  ↓ 1:N
跟进记录表(follow_up)
  ↓ 1:N
操作日志表(operation_log)
  ↓ 1:N
登录日志表(login_log)
```

### 建表脚本
执行文件: `db-setup-v2.sql`

**核心字段说明:**

#### 用户表 (user)
- **role**: ADMIN(系统管理), MANAGER(销售经理), USER(普通用户)
- **status**: 0-禁用, 1-启用
- **password**: BCrypt加密(默认密码: admin123 / user123)

#### 客户表 (customer)
- **status**: 0-删除, 1-正常, 2-已成交, 3-流失
- **level**: A/B/C/D (客户等级)
- **软删除**: 不物理删除，仅更新status=0

#### 跟进记录表 (follow_up)
- **type**: CALL/EMAIL/MEETING/WECHAT/OTHER
- **级联删除**: 删除客户时自动删除跟进记录

---

## API接口

### 统一响应格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": {...},
  "timestamp": "2025-12-18 10:00:00"
}
```

### 1. 认证模块 `/api/auth`

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

---

### 2. 客户管理 `/api/customers`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/customers` | GET | 分页查询 | 已登录 |
| `/customers/{id}` | GET | 详情 | 已登录 |
| `/customers` | POST | 新增 | 已登录 |
| `/customers/{id}` | PUT | 修改 | 自己或ADMIN |
| `/customers/{id}` | DELETE | 删除 | ADMIN |
| `/customers/{id}/transfer` | PUT | 转移归属 | 管理员 |

**查询参数:**
```
?page=1&pageSize=10&name=&company=&status=&level=&ownerId=
```

**新增示例:**
```json
{
  "name": "张三",
  "phone": "13800138000",
  "company": "某某科技"
}
```

---

### 3. 跟进记录 `/api/follow-ups`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/follow-ups/customer/{id}` | GET | 客户跟进历史 | 已登录 |
| `/follow-ups` | POST | 添加跟进 | 已登录 |
| `/follow-ups/{id}` | DELETE | 删除 | 自己或ADMIN |

**新增跟进:**
```json
{
  "customerId": 1,
  "type": "CALL",
  "content": "电话沟通",
  "result": "客户有意向下周拜访",
  "nextFollowTime": "2025-12-25 10:00:00"
}
```

---

### 4. 统计分析 `/api/stats`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/stats/dashboard` | GET | 数据看板 | 管理员 |

返回: 客户总数、新增客户、活跃客户、成交客户、7日趋势等

---

### 5. 日志管理 `/api/logs`

| 接口 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/logs/operations` | GET | 操作日志 | ADMIN |
| `/logs/login` | GET | 登录日志 | ADMIN |

---

## 权限设计

### 角色矩阵
| 功能 | ADMIN | MANAGER | USER |
|------|-------|---------|------|
| 用户管理 | ✅ | ❌ | ❌ |
| 查看所有客户 | ✅ | ✅ | ❌ |
| 查看自己客户 | ✅ | ✅ | ✅ |
| 新增/修改客户 | ✅ | ✅ | ✅ (仅自己) |
| 删除客户 | ✅ | ❌ | ❌ |
| 转移客户 | ✅ | ✅ | ❌ |
| 查看所有跟进 | ✅ | ✅ | ❌ |
| 查看自己跟进 | ✅ | ✅ | ✅ |
| 统计数据 | ✅ | ✅ | ❌ |
| 操作日志 | ✅ | ❌ | ❌ |

### 拦截器配置
- **拦截**: `/api/**` (排除登录注册)
- **验证**: JWT Token有效性
- **注入**: 用户信息到Request上下文

---

## 开发步骤

### ✅ Step 1: 创建实体类
在 `src/main/java/com/xiaozhounandu/entity/` 创建:
- `User.java`
- `Customer.java`
- `FollowUp.java`
- `OperationLog.java`
- `LoginLog.java`

### ✅ Step 2: 创建Mapper
在 `src/main/java/com/xiaozhounandu/mapper/` 创建:
- `UserMapper.java`
- `CustomerMapper.java`
- `FollowUpMapper.java`
- `OperationLogMapper.java`
- `LoginLogMapper.java`

在 `src/main/resources/mapper/` 创建对应的XML文件

### ✅ Step 3: 创建Service接口
在 `src/main/java/com/xiaozhounandu/service/` 创建:
- `AuthService.java`
- `CustomerService.java`
- `FollowUpService.java`
- `StatsService.java`
- `LogService.java`

### ✅ Step 4: 实现Service
在 `src/main/java/com/xiaozhounandu/service/impl/` 实现:
- `AuthServiceImpl.java`
- `CustomerServiceImpl.java`
- `FollowUpServiceImpl.java`
- `StatsServiceImpl.java`
- `LogServiceImpl.java`

**关键功能:**
- BCrypt密码加密验证
- 权限校验
- 数据验证
- 业务逻辑

### ✅ Step 5: 创建DTO
在 `src/main/java/com/xiaozhounandu/dto/` 创建:

**request包:**
- `LoginRequest.java`
- `RegisterRequest.java`
- `CustomerRequest.java`
- `CustomerQueryRequest.java`
- `FollowUpRequest.java`
- `PasswordUpdateRequest.java`

**response包:**
- `ApiResult.java` (统一响应)
- `UserInfoResponse.java`
- `DashboardResponse.java`
- `PageResponse.java`

### ✅ Step 6: 实现Controller
在 `src/main/java/com/xiaozhounandu/controller/` 创建:
- `AuthController.java`
- `CustomerController.java`
- `FollowUpController.java`
- `StatsController.java`
- `LogController.java`

**注意:**
- 使用 `@RestController`
- 使用 `@RequestMapping("/api/模块")`
- 参数校验使用 `@Valid`
- 获取当前用户: 从Request Attribute获取

### ✅ Step 7: 配置拦截器
在 `src/main/java/xiaozhounandu/interceptor/`:
```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        // 1. 获取Authorization header
        // 2. 验证Token
        // 3. 获取用户信息
        // 4. 存入request.setAttribute("currentUser", user)
        // 5. 返回true/false
    }
}
```

在 `src/main/java/xiaozhounandu/config/WebConfig.java`:
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/auth/**", "/api/test/**");
}
```

### ✅ Step 8: 配置CORS
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
}
```

### ✅ Step 9: 测试API
使用Postman或curl测试:
```bash
# 测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 测试客户列表 (携带Token)
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer <token>"
```

---

## 核心代码示例

### 1. BCrypt加密
```java
@Service
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && encoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
```

### 2. 客户查询SQL (Mappper XML)
```xml
<select id="selectByQuery" resultType="Customer">
  <where>
    <if test="name != null and name != ''">
      AND name LIKE CONCAT('%', #{name}, '%')
    </if>
    <if test="status != null">
      AND status = #{status}
    </if>
    <if test="ownerId != null">
      AND owner_id = #{ownerId}
    </if>
    AND status != 0
  </where>
  ORDER BY update_time DESC
  LIMIT #{offset}, #{pageSize}
</select>
```

### 3. 权限校验
```java
@PutMapping("/{id}")
public ApiResult<Void> update(@PathVariable Long id,
                              @RequestBody CustomerRequest request,
                              @RequestAttribute("currentUser") User currentUser) {
    // 获取原客户
    Customer old = customerService.getById(id);

    // 权限校验: 只能修改自己的客户，除非是ADMIN
    if (!currentUser.getRole().equals("ADMIN") &&
        !old.getOwnerId().equals(currentUser.getId())) {
        return ApiResult.error("无权修改");
    }

    customerService.update(id, request);
    return ApiResult.success("更新成功");
}
```

### 4. 日志记录
```java
public void logOperation(User user, String module, String operation,
                        Long targetId, String targetName,
                        Object before, Object after) {
    OperationLog log = new OperationLog();
    log.setUserId(user.getId());
    log.setModule(module);
    log.setOperation(operation);
    log.setTargetId(targetId);
    log.setTargetName(targetName);
    log.setBeforeData(before != null ? new ObjectMapper().writeValueAsString(before) : null);
    log.setAfterData(after != null ? new ObjectMapper().writeValueAsString(after) : null);
    log.setIpAddress(IpUtil.getClientIp(request));
    operationLogMapper.insert(log);
}
```

---

## 配置文件

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiaozhounandu_v2?useSSL=false&serverTimezone=UTF-8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

server:
  port: 8080

mybatis:
  mapper-locations: classpath*:mapper/*.xml
  type-aliases-package: com.xiaozhounandu.entity
  configuration:
    map-underscore-to-camel-case: true
```

---

## 开发注意事项

### ✅ 必须遵守
1. **密码必须使用BCrypt加密**，禁用MD5
2. **客户删除使用软删除**，status=0
3. **所有修改操作记录日志**
4. **参数必须校验**，防止SQL注入
5. **统一异常处理**，返回友好信息

### ⚠️ 常见问题
1. **跨域问题**: 确保CORS配置正确
2. **401错误**: 检查Token格式 `Bearer <token>`
3. **403错误**: 检查权限和角色
4. **JSON解析错误**: 检查Content-Type和DTO字段

---

## 测试账号

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 全部 |
| manager | admin123 | MANAGER | 客户管理+统计 |
| user | user123 | USER | 仅自己的客户 |

---

**数据库初始化脚本**: `db-setup-v2.sql`
**最后更新**: 2025-12-18
