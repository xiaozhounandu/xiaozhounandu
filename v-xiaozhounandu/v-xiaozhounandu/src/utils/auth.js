import request from './request'

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

// 获取当前用户信息
export function getCurrentUser() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (e) {
      return null
    }
  }
  return null
}

// 检查角色权限
export function hasRole(role) {
  const user = getCurrentUser()
  return user && user.role === role
}

// 检查是否是ADMIN
export function isAdmin() {
  return hasRole('ADMIN')
}

// 检查是否是MANAGER
export function isManager() {
  return hasRole('MANAGER')
}

// 用户登录
export async function login(username, password) {
  const res = await request.post('/api/auth/login', { username, password })

  if (res.success && res.data) {
    localStorage.setItem('token', res.data.token.replace('Bearer ', ''))
    localStorage.setItem('user', JSON.stringify(res.data.user))
    return res.data
  }
  throw new Error(res.message || '登录失败')
}

// 用户注册
export async function register(userData) {
  const res = await request.post('/api/auth/register', userData)

  if (res.success) {
    return res.data
  }
  throw new Error(res.message || '注册失败')
}

// 用户登出
export async function logout() {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      await request.post('/api/auth/logout')
    } catch (e) {
      console.error('登出API调用失败:', e)
    }
  }

  localStorage.removeItem('token')
  localStorage.removeItem('user')
  window.location.href = '/login'
}

// 修改密码
export async function updatePassword(oldPassword, newPassword) {
  const res = await request.put('/api/auth/password', { oldPassword, newPassword })

  if (res.success) {
    return res.data
  }
  throw new Error(res.message || '密码修改失败')
}

// 获取当前用户详情（从后端）
export async function getCurrentUserDetail() {
  const res = await request.get('/api/auth/current')

  if (res.success) {
    return res.data
  }
  throw new Error(res.message || '获取用户信息失败')
}
