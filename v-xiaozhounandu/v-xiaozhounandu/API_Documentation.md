# 客户管理系统 - API接口文档

## 📡 接口调用方式

所有API通过 Axios 封装调用，前端代理到 `http://localhost:8080`

```javascript
import {
  loginApi,
  getCustomersApi,
  createCustomerApi
} from '@/api/模块'

// 调用示例
const res = await getCustomersApi({ page: 1, size: 10 })
// res = { success: true, message: '成功', data: { list: [], total: 100, stats: {} } }
```

---

## 🔐 认证模块 (auth.js)

### 1. 登录
```javascript
loginApi(data)
// POST /api/auth/login
// data: { username, password }
// 返回: { token, user }
```

### 2. 注册
```javascript
registerApi(data)
// POST /api/auth/register
// data: { username, password, nickname, email, phone }
```

### 3. 登出
```javascript
logoutApi()
// POST /api/auth/logout
```

### 4. 获取当前用户
```javascript
getCurrentUserApi()
// GET /api/auth/current
// 返回: 用户完整信息
```

### 5. 修改密码
```javascript
updatePasswordApi(data)
// PUT /api/auth/password
// data: { oldPassword, newPassword }

changePasswordApi(data)
// PUT /api/auth/change-password
// data: { currentPassword, newPassword }
```

### 6. 更新个人资料
```javascript
updateProfileApi(data)
// PUT /api/auth/profile
// data: { realName, nickname, email, phone }
```

### 7. 获取个人统计
```javascript
getStatsApi()
// GET /api/auth/stats
// 返回: { customers, followups, todayFollowups, successRate }
```

### 8. 获取用户列表（简单）
```javascript
getSimpleUsersApi()
// GET /api/auth/simple-users
// 返回: [{ id, username, nickname, role }]
```

---

## 👥 客户管理 (customer.js)

### 1. 获取客户列表（分页+筛选）
```javascript
getCustomersApi(params)
// GET /api/customers
// params: {
//   page: 1,
//   size: 10,
//   keyword: '',     // 搜索客户名/电话
//   status: '',      // 筛选状态
//   source: ''       // 筛选来源
// }
// 返回: { list: [{ id, name, phone, status, source, ownerName, lastFollowupTime, createdAt }], total }
```

### 2. 获取详情
```javascript
getCustomerDetailApi(id)
// GET /api/customers/{id}
// 返回: {
//   customer: {...},
//   followups: [...],
//   stats: { totalFollowups, lastFollowupDays, followupTypes }
// }
```

### 3. 创建客户
```javascript
createCustomerApi(data)
// POST /api/customers
// data: {
//   name, phone, email, company,
//   status, source, address, remark,
//   ownerId (仅管理员可指定)
// }
```

### 4. 更新客户
```javascript
updateCustomerApi(id, data)
// PUT /api/customers/{id}
// data: 同创建
```

### 5. 删除客户
```javascript
deleteCustomerApi(id)
// DELETE /api/customers/{id}
```

### 6. 转移客户
```javascript
transferCustomerApi(data)
// POST /api/customers/transfer
// data: { customerId, targetUserId }
```

### 7. 获取可转移的负责人列表
```javascript
getAvailableOwnersApi()
// GET /api/customers/owners
// 返回: [{ id, username, nickname }]
```

---

## 📝 跟进记录 (followup.js)

### 1. 获取跟进列表
```javascript
getFollowupsApi(params)
// GET /api/followups
// params: {
//   page, size,
//   keyword,          // 搜索客户名/内容
//   type,             // 跟进类型
//   userId,           // 指定用户
//   startDate,        // 开始日期
//   endDate,          // 结束日期
//   id                // 单条查询
// }
// 返回: {
//   list: [{ id, customerId, customerName, type, content, userName, followTime, nextFollowTime }],
//   total,
//   stats
// }
```

### 2. 添加跟进
```javascript
addFollowupApi(data)
// POST /api/followups
// data: { customerId, type, content, nextFollowTime }
```

### 3. 更新跟进
```javascript
updateFollowupApi(id, data)
// PUT /api/followups/{id}
// data: { type, content, nextFollowTime }
```

### 4. 删除跟进
```javascript
deleteFollowupApi(id)
// DELETE /api/followups/{id}
```

### 5. 客户的跟进记录
```javascript
getCustomerFollowupsApi(customerId)
// GET /api/followups/customer/{customerId}
```

---

## 📊 统计分析 (stats.js)

### 1. 数据概览
```javascript
getStats()
// GET /api/stats
// 返回: {
//   totalCustomers: 150,      // 总客户数
//   customerGrowth: 12,       // 增长百分比
//   todayFollowups: 8,        // 今日跟进
//   weekAdded: 15,            // 本周新增
//   pendingFollowups: 3,      // 待跟进
//   statusDistribution: {     // 状态分布
//     potential: 20,
//     contacted: 50,
//     negotiating: 30,
//     success: 40,
//     failed: 10
//   },
//   recentFollowups: [        // 最近跟进
//     { id, customerName, type, content, userName, followTime }
//   ]
// }
```

---

## 📋 日志管理 (log.js)

### 1. 操作日志
```javascript
getOperationLogsApi(params)
// GET /api/logs/operations
// params: { page, size, keyword, operation, module, startDate, endDate }
// 返回: {
//   list: [{ id, username, realName, module, operation, details, ipAddress, createdAt, success }],
//   total
// }
```

### 2. 登录日志
```javascript
getLoginLogsApi(params)
// GET /api/logs/login
// params: { page, size, keyword, status, startDate, endDate }
// 返回: {
//   list: [{ id, username, realName, ipAddress, location, browser, loginTime, success, failureReason }],
//   total,
//   stats: { todayLogins, weekLogins, failedLogins }
// }
```

---

## 🎯 响应数据结构

### 成功响应
```javascript
{
  success: true,
  message: "操作成功",
  data: {
    // 具体数据
  }
}
```

### 失败响应
```javascript
{
  success: false,
  message: "错误提示信息",
  data: null
}
```

### 分页响应
```javascript
{
  success: true,
  message: "成功",
  data: {
    list: [
      // 数据列表
    ],
    total: 100,  // 总记录数
    stats: {}    // 额外统计
  }
}
```

---

## 📋 枚举值参考

### 客户状态
| 值 | 含义 |
|----|------|
| potential | 潜在客户 |
| contacted | 已联系 |
| negotiaging | 谈判中 |
| success | 已成交 |
| failed | 已流失 |

### 客户来源
| 值 | 含义 |
|----|------|
| online | 网络咨询 |
| referral | 客户推荐 |
| advertising | 广告投放 |
| offline | 线下活动 |

### 跟进类型
| 值 | 含义 |
|----|------|
| phone | 电话 |
| visit | 拜访 |
| email | 邮件 |
| wechat | 微信 |
| meeting | 会议 |

### 用户角色
| 值 | 含义 |
|----|------|
| ADMIN | 管理员 |
| MANAGER | 销售经理 |
| USER | 普通用户 |

### 操作类型
| 值 | 含义 |
|----|------|
| create | 创建 |
| update | 更新 |
| delete | 删除 |
| login | 登录 |
| logout | 登出 |
| transfer | 转移 |
| export | 导出 |
| import | 导入 |

### 日志模块
| 值 | 含义 |
|----|------|
| customer | 客户管理 |
| followup | 跟进记录 |
| user | 用户管理 |
| system | 系统管理 |

---

## 🔄 请求/响应示例

### 创建客户流程
```javascript
// 前端调用
const customerData = {
  name: "张三",
  phone: "13800138000",
  status: "potential",
  source: "online"
}
const res = await createCustomerApi(customerData)

// 后端返回
{
  success: true,
  message: "客户创建成功",
  data: { id: 123, ...customerData }
}
```

### 客户列表带分页
```javascript
// 前端调用
const params = {
  page: 1,
  size: 10,
  keyword: "张",
  status: "potential"
}
const res = await getCustomersApi(params)

// 后端返回
{
  success: true,
  data: {
    list: [
      { id: 1, name: "张三", phone: "138...", status: "potential" },
      { id: 2, name: "张伟", phone: "139...", status: "contacted" }
    ],
    total: 25
  }
}
```

### 跟进记录统计
```javascript
// 前端调用
const res = await getFollowupsApi({ page: 1, size: 20 })

// 后端返回包含统计数据
{
  success: true,
  data: {
    list: [...],
    total: 150,
    stats: {
      totalFollowups: 150,
      thisMonth: 45,
      pending: 8
    }
  }
}
```

---

## 🔒 权限控制表

| 接口 | ADMIN | MANAGER | USER | 说明 |
|------|-------|---------|------|------|
| `/api/auth/*` | ✅ | ✅ | ✅ | 认证接口（注册限制级别） |
| `/api/customers` | ✅ | ✅ | ✅ | 查看客户列表 |
| POST `/api/customers` | ✅ | ✅ | ✅ | 创建客户 |
| PUT `/api/customers/{id}` | ✅ | ✅ | ✅ | 编辑客户（限自己的） |
| DELETE `/api/customers/{id}` | ✅ | ✅ | ❌ | 删除客户（USER不能删除） |
| `/api/customers/transfer` | ✅ | ✅ | ❌ | 转移客户 |
| `/api/customers/owners` | ✅ | ✅ | ❌ | 获取用户列表 |
| `/api/followups` | ✅ | ✅ | ✅ | 查看跟进（限自己的） |
| POST `/api/followups` | ✅ | ✅ | ✅ | 添加跟进 |
| PUT `/api/followups/{id}` | ✅ | ✅ | ✅ | 编辑（限自己的） |
| DELETE `/api/followups/{id}` | ✅ | ✅ | ✅ | 删除（限自己的） |
| `/api/stats` | ✅ | ✅ | ✅ | 个人统计 |
| `/api/logs/*` | ✅ | ❌ | ❌ | 仅管理员可查看日志 |

---

## 💡 最佳实践

### 1. 错误处理
```javascript
try {
  const res = await getCustomersApi(params)
  if (res.success) {
    this.customers = res.data.list
  } else {
    alert(res.message)
  }
} catch (error) {
  console.error('请求失败:', error)
  alert('网络错误，请稍后重试')
}
```

### 2. 加载状态
```javascript
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    // 请求...
  } finally {
    loading.value = false
  }
}
```

### 3. 分页处理
```javascript
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function changePage(page) {
  if (page < 1 || page > Math.ceil(total.value / pageSize.value)) return
  currentPage.value = page
  await loadData()
}
```

### 4. 搜索防抖（可选）
```javascript
let searchTimer = null

function handleSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadData()
  }, 500)
}
```

---

## 🔧 调试技巧

### 前端调试
1. 浏览器开发者工具 → Network 标签
2. 查看请求头：`Authorization: Bearer token`
3. 查看响应数据格式
4. Console 查看错误信息

### 后端对接检查清单
- [ ] 后端服务启动在 localhost:8080
- [ ] 数据库连接正常
- [ ] 跨域配置允许前端域名
- [ ] Token验证逻辑正常
- [ ] 分页参数接收正常

### 常见问题
1. **401 Unauthorized**: Token未传或已过期
2. **403 Forbidden**: 权限不足
3. **404 Not Found**: 接口路径错误
4. **500 Server Error**: 后端代码异常
5. **CORS error**: 跨域配置问题

---

文档版本: v1.0
最后更新: 2025-12-18