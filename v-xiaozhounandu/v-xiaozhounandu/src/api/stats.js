import request from '../utils/request'

// 获取统计数据（Dashboard）
export function getStats() {
  return request.get('/api/stats/dashboard')
}

// 获取数据看板（别名）
export function getDashboardApi() {
  return request.get('/api/stats/dashboard')
}
