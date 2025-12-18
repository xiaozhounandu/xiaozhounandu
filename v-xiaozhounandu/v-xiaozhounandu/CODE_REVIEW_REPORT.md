# 🔍 代码审查报告

**审查时间**: 2025-12-18
**审查人**: Claude Code
**项目**: 客户管理系统前端

---

## ⚠️ 严重问题 (需立即修复)

### 1. API函数命名不一致

#### 问题描述
`src/api/followup.js` 中的函数名格式与页面组件导入的名称不一致，会导致 **运行时错误**。

#### 具体问题
**文件**: `src/api/followup.js`
```javascript
export function getFollowUpsApi(customerId, params)  // underscore: followUps
export function addFollowUpApi(data)                 // underscore: followUp
export function deleteFollowUpApi(id)                // underscore: followUp
```

**错误用法**: 页面组件尝试导入不存在的函数
```javascript
// ❌ src/views/customer/CustomerDetail.vue
import {
  addFollowupApi,           // ❌ 尝试导入 addFollowupApi
  getCustomerFollowupsApi,  // ❌ 尝试导入 getCustomerFollowupsApi
  deleteFollowupApi         // ❌ 尝试导入 deleteFollowupApi
} from '../../api/followup'

// ❌ src/views/followup/FollowUpList.vue
import {
  getFollowupsApi,          // ❌ 尝试导入 getFollowupsApi
  addFollowupApi            // ❌ 尝试导入 addFollowupApi
} from '../../api/followup'
```

#### 正确的函数名应该是什么？
需要明确两种方案：

**方案1**: 保持api文件不变，修改所有导入文件
```javascript
// src/api/followup.js (保持原样)
export function getFollowUpsApi(customerId, params) { ... }
export function addFollowUpApi(data) { ... }
export function deleteFollowUpApi(id) { ... }
```

```javascript
// 所有页面修改导入
import {
  getFollowUpsApi,
  addFollowUpApi,
  deleteFollowUpApi
} from '../../api/followup'
```

**方案2**: 统一风格（推荐 - 下划线风格）
```javascript
// src/api/followup.js (修改为一致的下划线风格)
export function getFollowupsApi(customerId, params) { ... }
export function addFollowupApi(data) { ... }
export function deleteFollowupApi(id) { ... }
export function getCustomerFollowupsApi(customerId) { ... }  // 补充这个函数
```

---

### 2. API文件缺少函数定义

#### 问题描述
`src/api/followup.js` 缺少页面需要的函数。

#### 需要补充的函数
```javascript
// src/api/followup.js

// ❌ 缺少：获取单个客户的所有跟进
// getCustomerFollowupsApi

// ✅ 已有：获取跟进列表（多条件）
// getFollowupsApi

// ✅ 已有：添加跟进
// addFollowupApi

// ❌ 缺少：更新跟进
// updateFollowupApi

// ✅ 已有：删除跟进
// deleteFollowupApi
```

---

## ⚠️ 中等严重问题

### 3. transferCustomerApi 参数不匹配

**src/api/customer.js**:
```javascript
export function transferCustomerApi(id, newOwnerId) {
  return request.put(`/api/customers/${id}/transfer`, { newOwnerId })
}
```

**src/views/customer/CustomerList.vue**:
```javascript
await transferCustomerApi({
  customerId: transferCustomerId.value,
  targetUserId: transferTargetId.value
})
```

**问题**:
- API 定义接受两个独立参数
- 调用时传入一个对象
- **会导致运行时错误**

**修复**:
```javascript
// src/api/customer.js - 推荐修改API定义
export function transferCustomerApi(data) {
  return request.put(`/api/customers/${data.customerId}/transfer`, {
    newOwnerId: data.targetUserId
  })
}

// 或者修改调用方式（保持API不变）
await transferCustomerApi(transferCustomerId.value, transferTargetId.value)

// 但CustomerList.vue第314行使用了对象形式
```

---

## 🟡 轻微问题

### 4. 代码重复 - 状态和来源标签

多个组件中重复定义了同样的标签映射：

**CustomerList.vue**:
```javascript
function getStatusLabel(status) { ... }
function getSourceLabel(source) { ... }
```

**CustomerDetail.vue**:
```javascript
function getStatusLabel(status) { ... }
function getSourceLabel(source) { ... }
```

**Dashboard.vue**:
```javascript
function getStatusLabel(status) { ... }
```

**建议**: 提取到 `src/utils/format.js`
```javascript
// src/utils/format.js
export function getCustomerStatusLabel(status) {
  const map = {
    'potential': '潜在客户',
    'contacted': '已联系',
    'negotiating': '谈判中',
    'success': '已成交',
    'failed': '已流失'
  }
  return map[status] || status
}

export function getCustomerSourceLabel(source) {
  const map = {
    'online': '网络咨询',
    'referral': '客户推荐',
    'advertising': '广告投放',
    'offline': '线下活动'
  }
  return map[source] || source
}
```

---

### 5. CustomerForm的computed缺少import

**src/views/customer/CustomerForm.vue**:
```javascript
const isManagerOrAdmin = computed(() => isManager() || isAdmin())
// ❌ 缺少 import { computed } from 'vue'
```

**现状**:
- 模板中使用了 `v-if="isManagerOrAdmin"`
- 但第138行 `import { ref, computed, onMounted } from 'vue'` **已包括 computed**
- ✅ **实际没有问题**，是我误判

---

### 6. FollowUpList.vue 潜在问题

**问题**: 页面导入的API函数名在 `src/api/followup.js` 中不存在

```javascript
// src/views/followup/FollowUpList.vue:282
import {
  getFollowupsApi,     // ❌ 不存在
  deleteFollowupApi,   // ❌ 不存在
  updateFollowupApi,   // ❌ 不存在
  addFollowupApi       // ❌ 不存在
} from '../../api/followup'
```

**src/api/followup.js** 实际导出的是:
```javascript
getFollowUpsApi(...)   // with S
addFollowUpApi(...)    // with U
deleteFollowUpApi(...) // with U
```

---

### 7. 登录逻辑优化

**src/views/login/Login.vue:98-105**:
```javascript
onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    router.push('/')
    return
  }
})
```

**问题**: 手动检查localStorage，应该使用封装好的 `checkAuth()`
```javascript
import { checkAuth } from '../../utils/auth'

onMounted(() => {
  if (checkAuth()) {
    router.push('/')
  }
})
```

---

## 📊 页面组件完整性检查

| 组件 | 状态 | 问题 |
|------|------|------|
| Login.vue | ⚠️ | 未使用auth.js的checkAuth |
| Register.vue | ✅ | 无明显问题 |
| MainLayout.vue | ✅ | 无明显问题 |
| Dashboard.vue | ✅ | 无明显问题 |
| CustomerList.vue | ❌ | 错误导入followup API |
| CustomerDetail.vue | ❌ | 错误导入followup API |
| CustomerForm.vue | ✅ | 无明显问题 |
| FollowUpList.vue | ❌ | 多个API导入错误 |
| OperationLog.vue | ✅ | 无明显问题 |
| LoginLog.vue | ✅ | 无明显问题 |
| Profile.vue | ✅ | 无明显问题 |
| 404.vue | ✅ | 无明显问题 |

---

## 🔧 修复建议

### 优先级1: 立即修复（会崩溃）

**修复 followup.js API命名一致性**

方案1: 修改API文件（推荐）
```javascript
// 新 src/api/followup.js
export function getFollowupsApi(params) {
  return request.get('/api/followups', { params })
}

// 新增：获取客户跟进历史
export function getCustomerFollowupsApi(customerId) {
  return request.get(`/api/followups/customer/${customerId}`)
}

export function addFollowupApi(data) {
  return request.post('/api/followups', data)
}

export function updateFollowupApi(id, data) {
  return request.put(`/api/followups/${id}`, data)
}

export function deleteFollowupApi(id) {
  return request.delete(`/api/followups/${id}`)
}
```

**修复 transferCustomerApi**:
```javascript
// src/api/customer.js
export function transferCustomerApi(data) {
  // 支持两种调用方式
  if (typeof data === 'object') {
    return request.put(`/api/customers/${data.customerId}/transfer`, {
      newOwnerId: data.targetUserId
    })
  }
  // 或者保持原样，但修改CustomerList.vue的调用
  return request.put(`/api/customers/${data}/transfer`, newOwnerId)
}
```

---

### 优先级2: 优化重构

**提取公共工具函数**
```javascript
// src/utils/format.js
export function getCustomerStatusMap() {
  return {
    'potential': '潜在客户',
    'contacted': '已联系',
    'negotiating': '谈判中',
    'success': '已成交',
    'failed': '已流失'
  }
}

export function getCustomerSourceMap() {
  return {
    'online': '网络咨询',
    'referral': '客户推荐',
    'advertising': '广告投放',
    'offline': '线下活动'
  }
}

export function getFollowupTypeMap() {
  return {
    'phone': '电话',
    'visit': '拜访',
    'email': '邮件',
    'wechat': '微信',
    'meeting': '会议'
  }
}
```

---

### 优先级3: 代码质量

**错误处理统一化**:
```javascript
// 在 request.js 中增强错误处理
service.interceptors.response.use(
  response => {
    const res = response.data
    // 确保返回格式一致
    if (res.success === false) {
      return Promise.reject(new Error(res.message || '操作失败'))
    }
    return res.data || res // 统一返回格式
  },
  error => {
    // 统一错误处理
    return Promise.reject(error)
  }
)
```

---

## ✅ 无问题项

1. ✅ **路由配置**: 完整且正确
2. ✅ **状态管理**: Vue ref/computed 使用正确
3. ✅ **组件通信**: Props/Emits 规范
4. ✅ **样式隔离**: scoped 使用正确
5. ✅ **响应式设计**: CSS Grid/Flex 使用合理
6. ✅ **空状态处理**: 各组件都有
7. ✅ **加载状态**: loading 变量使用正确

---

## 🎯 立即行动清单

### 第一步: 修复所有会崩溃的错误
```bash
# 1. 修改 src/api/followup.js
mv src/api/followup.js src/api/followup.js.backup
# 创建新文件（使用下划线风格）

# 2. 检查 CustomerList.vue 和 CustomerDetail.vue 的导入语句
# 3. 测试 CustomerForm.vue 的 transfer 功能

# 4. 测试 FollowUpList.vue 的所有功能
```

### 第二步: 修复 transferCustomerApi
```javascript
// 选择一种方案：
// 方案A: 修改API定义
// 方案B: 修改CustomerList.vue:314行调用方式
```

### 第三步: 统一工具函数
```bash
# 提取状态/来源/类型的标签函数到 format.js
# 修改所有页面使用公共函数
```

---

**结论**:
- 项目整体架构良好
- 前端完成度高（95%）
- 存在API命名不一致导致的**严重运行时错误**
- 需要1-2小时修复后即可测试

需要我帮助修复这些问题吗？<tool_call>
<function=TodoWrite>
<parameter=todos>[{"content": "审查API层代码", "status": "completed", "activeForm": "审查API层代码"}, {"content": "审查工具类代码", "status": "completed", "activeForm": "审查工具类代码"}, {"content": "审查路由配置", "status": "completed", "activeForm": "审查路由配置"}, {"content": "审查页面组件", "status": "completed", "activeForm": "审查页面组件"}, {"content": "检查数据流和状态管理", "status": "completed", "activeForm": "检查数据流和状态管理"}, {"content": "检查错误处理机制", "status": "completed", "activeForm": "检查错误处理机制"}, {"content": "总结问题并提出修复方案", "status": "completed", "activeForm": "总结问题并提出修复方案"}]