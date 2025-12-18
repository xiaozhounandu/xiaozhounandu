<template>
  <div class="data-management">
    <div class="page-header">
      <h2>📊 数据管理</h2>
      <p class="subtitle">管理客户数据，丰富数据看板展示</p>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <div class="action-card">
        <h3>👤 添加测试用户</h3>
        <div class="form-group">
          <input v-model="newUser.username" placeholder="用户名" class="form-input" />
          <input v-model="newUser.nickname" placeholder="昵称" class="form-input" />
          <select v-model="newUser.role" class="form-input">
            <option value="">选择角色</option>
            <option value="ADMIN">管理员</option>
            <option value="MANAGER">销售经理</option>
            <option value="USER">销售员</option>
          </select>
          <div class="form-hint">💡 默认密码: admin123</div>
          <button @click="addTestUser" class="btn-primary" :disabled="!isUserFormValid">
            添加用户
          </button>
        </div>
      </div>

      <div class="action-card">
        <h3>🏢 批量添加客户</h3>
        <div class="form-group">
          <input v-model="customerCount" type="number" placeholder="客户数量" min="1" max="100" class="form-input" />
          <button @click="generateBatchCustomers" class="btn-primary" :disabled="!customerCount">
            生成客户数据
          </button>
        </div>
      </div>

      <div class="action-card">
        <h3>📝 添加跟进记录</h3>
        <div class="form-group">
          <select v-model="followUp.customerId" class="form-input">
            <option value="">选择客户</option>
            <option v-for="customer in customerList" :key="customer.id" :value="customer.id">
              {{ customer.name }}
            </option>
          </select>
          <select v-model="followUp.type" class="form-input">
            <option value="">跟进方式</option>
            <option value="CALL">电话</option>
            <option value="EMAIL">邮件</option>
            <option value="MEETING">会议</option>
            <option value="WECHAT">微信</option>
            <option value="OTHER">其他</option>
          </select>
          <textarea v-model="followUp.content" placeholder="跟进内容" class="form-input" rows="3"></textarea>
          <button @click="addFollowUp" class="btn-primary" :disabled="!isFollowUpFormValid">
            添加跟进记录
          </button>
        </div>
      </div>
    </div>

    <!-- 数据状态 -->
    <div class="status-section">
      <div class="status-grid">
        <div class="status-item">
          <div class="status-label">用户总数</div>
          <div class="status-value">{{ stats.totalUsers || 0 }}</div>
        </div>
        <div class="status-item">
          <div class="status-label">客户总数</div>
          <div class="status-value">{{ stats.totalCustomers || 0 }}</div>
        </div>
        <div class="status-item">
          <div class="status-label">跟进记录</div>
          <div class="status-value">{{ stats.totalFollowUps || 0 }}</div>
        </div>
        <div class="status-item">
          <div class="status-label">最近更新</div>
          <div class="status-value">{{ formatDateTime(stats.lastUpdate) }}</div>
        </div>
      </div>
    </div>

    <!-- 操作结果 -->
    <div class="results-section" v-if="results.length > 0">
      <h3>📋 操作结果</h3>
      <div class="results-list">
        <div v-for="(result, index) in results" :key="index" :class="['result-item', result.type]">
          <span class="result-icon">{{ getIcon(result.type) }}</span>
          <span class="result-message">{{ result.message }}</span>
          <span class="result-time">{{ formatTime(result.time) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { formatDateTime } from '../../utils/format'

const newUser = ref({
  username: '',
  nickname: '',
  role: '',
  email: '',
  phone: ''
})

const customerCount = ref('')
const followUp = ref({
  customerId: '',
  type: '',
  content: ''
})

const customerList = ref([])
const stats = ref({})
const results = ref([])

const isUserFormValid = computed(() => {
  return newUser.value.username && newUser.value.nickname && newUser.value.role
})

const isFollowUpFormValid = computed(() => {
  return followUp.value.customerId && followUp.value.type && followUp.value.content
})

// 添加测试用户
async function addTestUser() {
  try {
    // 生成邮箱、手机号和默认密码
    newUser.value.email = `${newUser.value.username}@xiaozhounandu.com`
    newUser.value.phone = `138${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`
    newUser.value.password = 'admin123' // 默认密码

    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newUser.value)
    })

    const data = await response.json()

    if (data.code === 200) {
      addResult('success', `成功添加用户: ${newUser.value.nickname} (密码: admin123)`)
      // 重置表单
      newUser.value = { username: '', nickname: '', role: '', email: '', phone: '' }
      // 刷新统计
      loadStats()
    } else {
      addResult('error', `添加用户失败: ${data.message}`)
    }
  } catch (error) {
    addResult('error', `添加用户失败: ${error.message}`)
  }
}

// 批量生成客户数据
async function generateBatchCustomers() {
  try {
    const customers = []
    const industries = ['制造业', '科技', '零售', '金融', '房地产', '食品', '物流', '教育', '医疗']
    const levels = ['A', 'B', 'C', 'D']
    const statuses = [1, 1, 1, 1, 1, 2, 2, 3] // 1-正常, 2-成交, 3-流失 (大部分正常)

    for (let i = 0; i < customerCount.value; i++) {
      customers.push({
        name: `测试客户${Date.now()}_${i}`,
        phone: `139${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`,
        email: `customer${Date.now()}_${i}@example.com`,
        industry: industries[Math.floor(Math.random() * industries.length)],
        address: `测试地址${i}`,
        ownerId: Math.floor(Math.random() * 3) + 1, // 随机分配给前3个用户
        level: levels[Math.floor(Math.random() * levels.length)],
        status: statuses[Math.floor(Math.random() * statuses.length)],
        remark: `批量生成的测试客户${i}`
      })
    }

    const response = await fetch('/api/customers/batch', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(customers)
    })

    const data = await response.json()

    if (data.code === 200) {
      addResult('success', `成功生成 ${customerCount.value} 个客户`)
      customerCount.value = ''
      loadStats()
      loadCustomers()
    } else {
      addResult('error', `生成客户失败: ${data.message}`)
    }
  } catch (error) {
    addResult('error', `生成客户失败: ${error.message}`)
  }
}

// 添加跟进记录
async function addFollowUp() {
  try {
    const response = await fetch('/api/followups', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        customerId: followUp.value.customerId,
        content: followUp.value.content,
        type: followUp.value.type,
        nextTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString() // 7天后
      })
    })

    const data = await response.json()

    if (data.code === 200) {
      addResult('success', '成功添加跟进记录')
      followUp.value = { customerId: '', type: '', content: '' }
      loadStats()
    } else {
      addResult('error', `添加跟进记录失败: ${data.message}`)
    }
  } catch (error) {
    addResult('error', `添加跟进记录失败: ${error.message}`)
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const response = await fetch('/api/stats/summary', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const data = await response.json()
    if (data.code === 200) {
      stats.value = data.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载客户列表
async function loadCustomers() {
  try {
    const response = await fetch('/api/customers/user', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const data = await response.json()
    if (data.code === 200) {
      customerList.value = data.data.slice(0, 20) // 只显示前20个
    }
  } catch (error) {
    console.error('加载客户列表失败:', error)
  }
}

// 添加操作结果
function addResult(type, message) {
  results.value.unshift({
    type,
    message,
    time: new Date()
  })

  // 保持最多20条记录
  if (results.value.length > 20) {
    results.value = results.value.slice(0, 20)
  }
}

// 获取图标
function getIcon(type) {
  const icons = {
    success: '✅',
    error: '❌',
    warning: '⚠️',
    info: 'ℹ️'
  }
  return icons[type] || '📝'
}

// 格式化时间
function formatTime(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

onMounted(() => {
  loadStats()
  loadCustomers()
})
</script>

<style scoped>
.data-management {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.page-header h2 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 700;
}

.subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.action-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.action-card h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.btn-primary {
  background: #3498db;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-hint {
  font-size: 12px;
  color: #666;
  margin: 4px 0;
  padding: 4px 8px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #3498db;
}

.status-section {
  margin-bottom: 24px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.status-item {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  text-align: center;
}

.status-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.status-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.results-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.results-section h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.results-list {
  max-height: 300px;
  overflow-y: auto;
}

.result-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 12px;
}

.result-item:last-child {
  border-bottom: none;
}

.result-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.result-message {
  flex: 1;
  font-size: 14px;
  color: #555;
}

.result-time {
  font-size: 12px;
  color: #999;
}

.result-item.success .result-message {
  color: #27ae60;
}

.result-item.error .result-message {
  color: #e74c3c;
}

.result-item.warning .result-message {
  color: #f39c12;
}

@media (max-width: 768px) {
  .quick-actions {
    grid-template-columns: 1fr;
  }

  .status-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>