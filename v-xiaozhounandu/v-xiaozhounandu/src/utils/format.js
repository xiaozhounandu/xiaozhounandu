// 日期格式化
export function formatDate(date, pattern = 'yyyy-MM-dd') {
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

// 日期时间格式化
export function formatDateTime(date) {
  return formatDate(date, 'yyyy-MM-dd HH:mm:ss')
}

// 手机号脱敏
export function maskPhone(phone) {
  if (!phone || phone.length < 7) return phone
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

// 邮箱脱敏
export function maskEmail(email) {
  if (!email || !email.includes('@')) return email
  const [name, domain] = email.split('@')
  if (name.length <= 2) return `*${name}@${domain}`
  return name.substring(0, 2) + '***' + name.substring(name.length - 1) + '@' + domain
}

// 状态标签
export function getStatusLabel(status, type = 'customer') {
  if (type === 'customer') {
    const map = {
      1: { text: '正常', type: 'success' },
      0: { text: '已删除', type: 'danger' },
      2: { text: '已成交', type: 'primary' },
      3: { text: '已流失', type: 'warning' }
    }
    return map[status] || { text: '未知', type: 'info' }
  } else if (type === 'login') {
    return status === 1 ? { text: '成功', type: 'success' } : { text: '失败', type: 'danger' }
  } else if (type === 'operation') {
    return status === 1 ? { text: '成功', type: 'success' } : { text: '失败', type: 'danger' }
  }
}

// 等级标签
export function getLevelLabel(level) {
  const map = {
    'A': { text: 'A级', type: 'danger' },
    'B': { text: 'B级', type: 'warning' },
    'C': { text: 'C级', type: 'info' },
    'D': { text: 'D级', type: 'success' }
  }
  return map[level] || { text: level, type: 'info' }
}

// 角色标签
export function getRoleLabel(role) {
  const map = {
    'ADMIN': { text: '管理员', type: 'danger' },
    'MANAGER': { text: '销售经理', type: 'warning' },
    'USER': { text: '用户', type: 'info' }
  }
  return map[role] || { text: role, type: 'info' }
}

// 跟进类型标签
export function getFollowUpTypeLabel(type) {
  const map = {
    'CALL': { text: '电话', type: 'primary' },
    'EMAIL': { text: '邮件', type: 'info' },
    'MEETING': { text: '拜访', type: 'success' },
    'WECHAT': { text: '微信', type: 'success' },
    'OTHER': { text: '其他', type: 'warning' }
  }
  return map[type] || { text: type, type: 'info' }
}

// 金额格式化
export function formatMoney(amount) {
  if (amount === null || amount === undefined) return '0.00'
  return Number(amount).toFixed(2)
}

// 分页计算
export function calculatePages(total, pageSize) {
  if (!total || !pageSize) return 1
  return Math.ceil(total / pageSize)
}
