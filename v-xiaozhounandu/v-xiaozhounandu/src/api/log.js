import request from '../utils/request'

// 操作日志
export function getOperationLogsApi(params) {
  return request.get('/api/logs/operations', { params })
}

// 登录日志
export function getLoginLogsApi(params) {
  return request.get('/api/logs/login', { params })
}
