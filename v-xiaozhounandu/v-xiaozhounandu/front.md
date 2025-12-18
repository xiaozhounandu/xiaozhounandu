# 客户管理系统 V2.0 - 前端开发文档

> 基于 Vue 3 + Vite + Vue Router 4

---

## 📋 目录
1. [技术栈](#技术栈)
2. [项目结构](#项目结构)
3. [路由设计](#路由设计)
4. [API接口封装](#api接口封装)
5. [核心组件](#核心组件)
6. [开发步骤](#开发步骤)

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.25 | 核心框架 |
| Vite | 7.2.4 | 构建工具 |
| Vue Router | 4.6.3 | 路由管理 |
| Axios | - | HTTP请求 |
| Pinia | - | 状态管理 (可选) |

---

## 项目结构

```
src/
├── main.js                    # 入口文件
├── App.vue                    # 根组件
│
├── router/index.js            # 路由配置 + 守卫
├── store/                     # Pinia状态管理 (可选)
│   ├── index.js
│   ├── user.js
│   └── customer.js
│
├── api/                       # API接口封装
│   ├── auth.js
│   ├── customer.js
│   ├── followup.js
│   ├── stats.js
│   └── log.js
│
├── utils/                     # 工具类
│   ├── request.js             # Axios封装
│   ├── auth.js                # Token管理
│   └── format.js              # 格式化工具
│
├── views/                     # 页面视图
│   ├── login/
│   │   ├── Login.vue          # 登录页
│   │   └── Register.vue       # 注册页
│   ├── layout/
│   │   ├── MainLayout.vue     # 主布局
│   │   ├── Header.vue         # 头部导航
│   │   └── Sidebar.vue        # 侧边栏
│   ├── dashboard/
│   │   └── Dashboard.vue      # 仪表盘
│   ├── customer/
│   │   ├── CustomerList.vue   # 客户列表
│   │   ├── CustomerDetail.vue # 客户详情
│   │   └── CustomerForm.vue   # 新增/编辑表单
│   ├── followup/
│   │   ├── FollowUpList.vue   # 跟进列表
│   │   └── FollowUpForm.vue   # 跟进表单
│   ├── system/
│   │   ├── OperationLog.vue   # 操作日志
│   │   └── LoginLog.vue       # 登录日志
│   └── user/
│       └── Profile.vue        # 个人中心
│
├── components/                # 公共组件
│   ├── common/
│   │   ├── Pagination.vue     # 分页组件
│   │   ├── SearchBar.vue      # 搜索栏
│   │   ├── Table.vue          # 表格
│   │   ├── Modal.vue          # 模态框
│   │   ├── Loading.vue        # 加载中
│   │   └── Empty.vue          # 空状态
│   └── form/
│       ├── InputField.vue     # 输入框封装
│       └── SelectField.vue    # 下拉框封装
│
└── assets/                    # 静态资源
    ├── css/
    │   ├── base.css           # 基础样式
    │   └── layout.css         # 布局样式
    └── logo.svg
```

---

## 路由设计

### 路由配置 (router/index.js)

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { checkAuth } from '../utils/auth'

const routes = [
  // 公开路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/Register.vue'),
    meta: { requiresAuth: false }
  },

  // 需要认证的路由 (主布局)
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '数据看板', icon: 'dashboard' }
      },
      {
        path: 'customers',
        name: 'CustomerList',
        component: () => import('@/views/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'customers' }
      },
      {
        path: 'customers/detail/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/customer/CustomerDetail.vue'),
        meta: { title: '客户详情', hidden: true }
      },
      {
        path: 'customers/form/:id?',
        name: 'CustomerForm',
        component: () => import('@/views/customer/CustomerForm.vue'),
        meta: { title: '客户表单', hidden: true }
      },
      {
        path: 'followups',
        name: 'FollowUpList',
        component: () => import('@/views/followup/FollowUpList.vue'),
        meta: { title: '跟进记录', icon: 'followup' }
      },
      {
        path: 'system/logs/operations',
        name: 'OperationLog',
        component: () => import('@/views/system/OperationLog.vue'),
        meta: { title: '操作日志', icon: 'log', roles: ['ADMIN'] }
      },
      {
        path: 'system/logs/login',
        name: 'LoginLog',
        component: () => import('@/views/system/LoginLog.vue'),
        meta: { title: '登录日志', icon: 'login-log', roles: ['ADMIN'] }
      },
      {
        path: 'user/profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = checkAuth()

  if (requiresAuth && !isAuthenticated) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (to.path === '/login' && isAuthenticated) {
    next({ path: '/' })
    return
  }

  // 角色权限检查
  if (to.meta.roles) {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!to.meta.roles.includes(user.role)) {
      alert('无权访问该页面')
      next(false)
      return
    }
  }

  next()
})

export default router
```

---

## API接口封装

### 1. Axios基础封装 (utils/request.js)

```javascript
import axios from 'axios'
import router from '../router'
import { logout } from './auth'

const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (!res.success) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        Message.error('登录已过期，请重新登录')
        logout()
      } else if (status === 403) {
        Message.error('无权访问')
      } else {
        Message.error('系统错误')
      }
    }
    return Promise.reject(error)
  }
)

export default service
```

### 2. 认证接口 (api/auth.js)

```javascript
import request from '../utils/request'

// 登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 注册
export function register(data) {
  return request.post('/auth/register', data)
}

// 登出
export function logout() {
  return request.post('/auth/logout')
}

// 获取当前用户
export function getCurrentUser() {
  return request.get('/auth/current')
}

// 修改密码
export function updatePassword(data) {
  return request.put('/auth/password', data)
}
```

### 3. 客户接口 (api/customer.js)

```javascript
import request from '../utils/request'

// 分页查询
export function getCustomers(params) {
  return request.get('/customers', { params })
}

// 获取详情
export function getCustomerDetail(id) {
  return request.get(`/customers/${id}`)
}

// 新增
export function addCustomer(data) {
  return request.post('/customers', data)
}

// 修改
export function updateCustomer(id, data) {
  return request.put(`/customers/${id}`, data)
}

// 删除
export function deleteCustomer(id) {
  return request.delete(`/customers/${id}`)
}

// 转移归属
export function transferCustomer(id, newOwnerId) {
  return request.put(`/customers/${id}/transfer`, { newOwnerId })
}
```

### 4. 跟进记录接口 (api/followup.js)

```javascript
import request from '../utils/request'

// 获取客户跟进历史
export function getFollowUps(customerId, params) {
  return request.get(`/follow-ups/customer/${customerId}`, { params })
}

// 添加跟进
export function addFollowUp(data) {
  return request.post('/follow-ups', data)
}

// 删除跟进
export function deleteFollowUp(id) {
  return request.delete(`/follow-ups/${id}`)
}
```

### 5. 统计接口 (api/stats.js)

```javascript
import request from '../utils/request'

// 获取仪表盘数据
export function getDashboard() {
  return request.get('/stats/dashboard')
}
```

### 6. 日志接口 (api/log.js)

```javascript
import request from '../utils/request'

// 操作日志
export function getOperationLogs(params) {
  return request.get('/logs/operations', { params })
}

// 登录日志
export function getLoginLogs(params) {
  return request.get('/logs/login', { params })
}
```

---

## 核心工具类

### 1. Auth工具 (utils/auth.js)

```javascript
// 检查登录状态
export function checkAuth() {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')

  if (!token || !user) return false

  try {
    JSON.parse(user)
    return true
  } catch {
    return false
  }
}

// 获取当前用户
export function getCurrentUser() {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : null
}

// 检查角色
export function hasRole(role) {
  const user = getCurrentUser()
  return user && user.role === role
}

// 检查是管理员
export function isAdmin() {
  return hasRole('ADMIN')
}

// 登出
export function logout() {
  const token = localStorage.getItem('token')
  if (token) {
    // 调用后端登出接口
    import('./request').then(({ default: axios }) => {
      axios.post('/auth/logout')
    })
  }

  localStorage.removeItem('token')
  localStorage.removeItem('user')
  window.location.href = '/login'
}
```

### 2. 格式化工具 (utils/format.js)

```javascript
// 日期格式化
export function formatDate(date, pattern = 'yyyy-MM-dd HH:mm:ss') {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return pattern.replace('yyyy', year)
                .replace('MM', month)
                .replace('dd', day)
                .replace('HH', hours)
                .replace('mm', minutes)
                .replace('ss', seconds)
}

// 手机号脱敏
export function maskPhone(phone) {
  if (!phone || phone.length < 11) return phone
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

// 状态标签
export function getStatusLabel(status) {
  const map = {
    0: { text: '已删除', type: 'danger' },
    1: { text: '正常', type: 'success' },
    2: { text: '已成交', type: 'primary' },
    3: { text: '已流失', type: 'warning' }
  }
  return map[status] || { text: '未知', type: 'info' }
}

// 等级标签
export function getLevelLabel(level) {
  const map = {
    'A': { text: 'A级 (重要)', type: 'danger' },
    'B': { text: 'B级 (一般)', type: 'warning' },
    'C': { text: 'C级 (潜在)', type: 'info' },
    'D': { text: 'D级 (普通)', type: 'success' }
  }
  return map[level] || { text: level, type: 'info' }
}
```

---

## 核心组件

### 1. 分页组件 (components/common/Pagination.vue)

```vue
<template>
  <div class="pagination">
    <button :disabled="currentPage === 1" @click="changePage(1)">首页</button>
    <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>

    <span class="page-info">
      第 {{ currentPage }} / {{ totalPages }} 页
    </span>

    <button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    <button :disabled="currentPage === totalPages" @click="changePage(totalPages)">尾页</button>

    <select v-model="pageSize" @change="changePageSize">
      <option :value="10">10条/页</option>
      <option :value="20">20条/页</option>
      <option :value="50">50条/页</option>
    </select>
  </div>
</template>

<script setup>
const props = defineProps({
  currentPage: Number,
  totalPages: Number,
  pageSize: Number
})

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'change'])

function changePage(page) {
  if (page < 1 || page > props.totalPages) return
  emit('update:currentPage', page)
  emit('change')
}

function changePageSize() {
  emit('update:pageSize', props.pageSize)
  emit('update:currentPage', 1)
  emit('change')
}
</script>

<style scoped>
.pagination {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
}
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-info {
  margin: 0 15px;
}
</style>
```

### 2. 搜索组件 (components/common/SearchBar.vue)

```vue
<template>
  <div class="search-bar">
    <div class="search-inputs">
      <input
        v-for="field in fields"
        :key="field.key"
        v-model="query[field.key]"
        :placeholder="field.placeholder"
        @keyup.enter="$emit('search')"
      />
      <button @click="$emit('search')" class="btn-search">搜索</button>
      <button @click="reset" class="btn-reset">重置</button>
    </div>
    <div class="actions" v-if="$slots.default">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  fields: Array, // [{key: 'name', placeholder: '姓名'}]
  modelValue: Object
})

const query = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

function reset() {
  Object.keys(query.value).forEach(key => query.value[key] = '')
  emit('search')
}
</script>
```

---

## 开发步骤

### ✅ Step 1: 环境准备

```bash
# 进入项目目录
cd /Users/weizhijie/Desktop/xiaozhounandu-main/v-xiaozhounandu/v-xiaozhounandu

# 安装依赖 (如果需要)
npm install axios
```

### ✅ Step 2: 创建目录结构

```
src/
├── views/
│   ├── login/
│   │   └── Login.vue
│   ├── layout/
│   │   └── MainLayout.vue
│   ├── customer/
│   │   └── CustomerList.vue
│   └── dashboard/
│       └── Dashboard.vue
├── api/
│   └── auth.js
├── utils/
│   ├── request.js
│   └── auth.js
└── router/
    └── index.js
```

### ✅ Step 3: 创建工具类

1. **utils/request.js** - Axios封装
2. **utils/auth.js** - 认证工具
3. **utils/format.js** - 格式化工具

### ✅ Step 4: 创建API封装

1. **api/auth.js** - 认证API
2. **api/customer.js** - 客户API
3. **api/followup.js** - 跟进API
4. **api/stats.js** - 统计API

### ✅ Step 5: 配置路由 (router/index.js)

1. 定义路由规则
2. 配置路由守卫
3. 配置权限控制

### ✅ Step 6: 创建登录页面

**views/login/Login.vue**
- 表单验证
- 登录逻辑
- 错误处理
- 跳转回原页面

### ✅ Step 7: 创建主布局

**views/layout/MainLayout.vue**
- 侧边栏导航
- 头部信息
- 内容区域

**views/layout/Header.vue**
- 显示当前用户
- 退出登录按钮

**views/layout/Sidebar.vue**
- 菜单项渲染
- 根据角色显示不同菜单

### ✅ Step 8: 创建数据看板

**views/dashboard/Dashboard.vue**
- 调用 `getDashboard()` API
- 显示统计卡片
- 图表展示 (7日趋势、客户分布)

### ✅ Step 9: 创建客户管理页面

**views/customer/CustomerList.vue**
- 搜索栏组件
- 表格展示客户列表
- 分页组件
- 新增/编辑/删除操作
- 详情跳转

**views/customer/CustomerForm.vue**
- 表单验证
- 新增/编辑复用
- 提交逻辑

**views/customer/CustomerDetail.vue**
- 客户基本信息
- 跟进记录列表
- 添加跟进按钮

### ✅ Step 10: 创建跟进页面

**views/followup/FollowUpList.vue**
- 全局跟进列表
- 按客户筛选
- 跟进类型统计

### ✅ Step 11: 创建日志页面 (仅ADMIN)

**views/system/OperationLog.vue**
- 操作日志查询
- 条件筛选
- 分页展示

**views/system/LoginLog.vue**
- 登录日志查询
- 成功/失败筛选

### ✅ Step 12: 添加公共组件

1. **Pagination.vue** - 分页组件
2. **SearchBar.vue** - 搜索组件
3. **Table.vue** - 表格组件
4. **Modal.vue** - 模态框组件 (新增/编辑用)
5. **Loading.vue** - 加载状态
6. **Empty.vue** - 空状态

### ✅ Step 13: 样式优化

**assets/css/base.css**
- CSS变量定义
- 基础样式重置

**assets/css/layout.css**
- 布局样式
- 响应式设计

### ✅ Step 14: 测试与优化

1. 测试所有路由跳转
2. 测试所有API调用
3. 测试权限控制
4. 优化页面加载速度
5. 添加错误处理

---

## 核心页面实现要点

### 1. 登录页 (Login.vue)

```vue
<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))

    // 跳转到原页面或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>
```

### 2. 客户列表页 (CustomerList.vue)

```vue
<script setup>
import { ref, onMounted, watch } from 'vue'
import { getCustomers, deleteCustomer } from '@/api/customer'
import { getCurrentUser } from '@/utils/auth'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref({ name: '', company: '', status: '' })
const loading = ref(false)

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getCustomers({
      page: page.value,
      pageSize: pageSize.value,
      ...query.value
    })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 删除客户
async function handleDelete(id) {
  if (!confirm('确定要删除吗？')) return
  await deleteCustomer(id)
  loadData()
}

onMounted(() => loadData())
watch([page, pageSize], loadData)
</script>
```

### 3. 数据看板 (Dashboard.vue)

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/stats'

const stats = ref({
  totalCustomers: 0,
  newCustomers: 0,
  activeCustomers: 0,
  dealedCustomers: 0,
  recent7Days: [],
  byIndustry: []
})
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    stats.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <h3>总客户数</h3>
        <div class="number">{{ stats.totalCustomers }}</div>
      </div>
      <div class="stat-card">
        <h3>本月新增</h3>
        <div class="number">{{ stats.newCustomers }}</div>
      </div>
      <!-- 更多卡片... -->
    </div>
  </div>
</template>
```

---

## 样式设计指南

### 颜色变量 (base.css)

```css
:root {
  --primary: #409eff;
  --success: #67c23a;
  --warning: #e6a23c;
  --danger: #f56c6c;
  --info: #909399;

  --bg-primary: #ffffff;
  --bg-secondary: #f5f5f5;
  --border: #dcdfe6;
  --text-primary: #303133;
  --text-secondary: #606266;
}
```

### 布局原则
- **侧边栏**: 固定宽度 200px
- **主内容**: 自适应宽度
- **头部**: 高度 60px
- **内边距**: 页面统一 20px

### 响应式
```css
@media (max-width: 768px) {
  .sidebar {
    display: none;
  }
}
```

---

## 性能优化

### 1. 路由懒加载
```javascript
const CustomerList = () => import('@/views/customer/CustomerList.vue')
```

### 2. 组件异步引入
```javascript
import { defineAsyncComponent } from 'vue'
const TableComponent = defineAsyncComponent(() => import('@/components/Table.vue'))
```

### 3. 防抖处理
```javascript
import { debounce } from 'lodash-es'
const search = debounce(() => loadData(), 500)
```

### 4. 缓存数据
```javascript
import { ref } from 'vue'
const cache = new Map()

async function getData(key, fetch) {
  if (cache.has(key)) return cache.get(key)
  const data = await fetch()
  cache.set(key, data)
  return data
}
```

---

## 常用代码片段

### 表单验证
```javascript
function validateForm() {
  if (!form.value.name) {
    alert('请输入客户姓名')
    return false
  }
  if (form.value.phone && !/^1[3-9]\d{9}$/.test(form.value.phone)) {
    alert('手机号格式错误')
    return false
  }
  return true
}
```

### 状态管理 (Pinia)
```javascript
// store/user.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    info: null,
    token: localStorage.getItem('token')
  }),
  actions: {
    async login(data) {
      // ...
    }
  }
})
```

### 权限控制
```vue
<template>
  <button v-if="hasRole('ADMIN')">删除</button>
</template>

<script setup>
import { hasRole } from '@/utils/auth'
</script>
```

---

## 测试要点

### 功能测试
- [ ] 登录/注册/登出
- [ ] 客户增删改查
- [ ] 跟进记录添加
- [ ] 数据看板显示
- [ ] 日志查询 (管理员)

### 权限测试
- [ ] 游客无法访问内部页面
- [ ] USER只能查看-/操作自己的客户
- [ ] ADMIN可以查看所有数据

### 边界测试
- [ ] 空数据展示
- [ ] 分页跳转
- [ ] 搜索无结果
- [ ] 网络异常处理

---

## 环境配置

### package.json 需要添加的依赖
```json
{
  "dependencies": {
    "vue": "^3.5.25",
    "vue-router": "^4.6.3",
    "axios": "^1.7.9",
    "pinia": "^2.3.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^6.0.2",
    "vite": "^7.2.4"
  }
}
```

### vite.config.js (如果需要代理)
```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

## 快速开始

```bash
# 1. 安装依赖
npm install axios pinia

# 2. 创建目录结构
mkdir -p src/views/{login,layout,customer,followup,system,dashboard,user}
mkdir -p src/{api,utils,components/common,components/form}

# 3. 创建核心文件
# 按照上面的步骤逐个创建

# 4. 启动开发服务器
npm run dev
```

---

**注意**:
- 所有API请求都需要携带Token
- 页面加载时检查登录状态
- 错误统一处理
- 保持代码简洁，避免过度封装

**最后更新**: 2025-12-18
