import request from '../utils/request'

// 分页查询客户
export function getCustomersApi(params) {
  return request.get('/api/customers', { params })
}

// 获取客户详情
export function getCustomerDetailApi(id) {
  return request.get(`/api/customers/${id}`)
}

// 新增客户（别名兼容）
export function addCustomerApi(data) {
  return request.post('/api/customers', data)
}

// 创建客户（同addCustomerApi，为了命名统一）
export function createCustomerApi(data) {
  return request.post('/api/customers', data)
}

// 修改客户
export function updateCustomerApi(id, data) {
  return request.put(`/api/customers/${id}`, data)
}

// 删除客户
export function deleteCustomerApi(id) {
  return request.delete(`/api/customers/${id}`)
}

// 获取可转移的负责人列表（经理/管理员可用）
export function getAvailableOwnersApi() {
  return request.get('/api/auth/simple-users')
}

// 转移客户（支持两种调用方式）
export function transferCustomerApi(data) {
  if (arguments.length === 2) {
    // 兼容旧方式: transferCustomerApi(customerId, targetUserId)
    const customerId = arguments[0]
    const targetUserId = arguments[1]
    return request.put(`/api/customers/${customerId}/transfer`, { newOwnerId: targetUserId })
  }
  // 新方式: transferCustomerApi({ customerId, targetUserId })
  return request.put(`/api/customers/${data.customerId}/transfer`, { newOwnerId: data.targetUserId })
}
