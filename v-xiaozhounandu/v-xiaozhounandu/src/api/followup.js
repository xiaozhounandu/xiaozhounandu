import request from '../utils/request'

// 获取跟进列表（多条件筛选）
export function getFollowupsApi(params) {
  return request.get('/api/followups', { params })
}

// 获取客户跟进历史
export function getCustomerFollowupsApi(customerId) {
  return request.get(`/api/followups/customer/${customerId}`)
}

// 添加跟进
export function addFollowupApi(data) {
  return request.post('/api/followups', data)
}

// 更新跟进
export function updateFollowupApi(id, data) {
  return request.put(`/api/followups/${id}`, data)
}

// 删除跟进
export function deleteFollowupApi(id) {
  return request.delete(`/api/followups/${id}`)
}
