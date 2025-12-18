import request from '../utils/request'

// 登录
export function loginApi(data) {
  return request.post('/api/auth/login', data)
}

// 注册
export function registerApi(data) {
  return request.post('/api/auth/register', data)
}

// 登出
export function logoutApi() {
  return request.post('/api/auth/logout')
}

// 获取当前用户
export function getCurrentUserApi() {
  return request.get('/api/auth/current')
}

// 修改密码
export function updatePasswordApi(data) {
  return request.put('/api/auth/password', data)
}

// 更新个人资料
export function updateProfileApi(data) {
  return request.put('/api/auth/profile', data)
}

// 修改密码（个人中心用）
export function changePasswordApi(data) {
  return request.put('/api/auth/change-password', data)
}

// 获取个人统计信息
export function getStatsApi() {
  return request.get('/api/auth/stats')
}

// 获取简单用户列表（用于下拉选择）
export function getSimpleUsersApi() {
  return request.get('/api/auth/simple-users')
}
