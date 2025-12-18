<template>
  <div class="customer-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <button class="btn-back" @click="handleBack">← 返回</button>
        <h2>客户详情</h2>
      </div>
      <div class="header-right" v-if="customer">
        <button class="btn-edit" @click="handleEdit">✏️ 编辑</button>
        <button class="btn-primary" @click="handleAddFollowup">📝 新建跟进</button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载客户信息...</p>
    </div>

    <div v-else-if="customer" class="detail-content">
      <!-- 基本信息面板 -->
      <div class="info-card">
        <div class="card-header">
          <h3>📋 基本信息</h3>
        </div>
        <div class="card-body">
          <div class="info-grid">
            <div class="info-item">
              <label>客户名称</label>
              <span>{{ customer.name }}</span>
            </div>
            <div class="info-item">
              <label>手机号</label>
              <span>{{ customer.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <label>邮箱</label>
              <span>{{ customer.email || '-' }}</span>
            </div>
            <div class="info-item">
              <label>公司</label>
              <span>{{ customer.company || '-' }}</span>
            </div>
            <div class="info-item">
              <label>状态</label>
              <span>
                <span class="status-badge" :class="'status-' + customer.status">
                  {{ getStatusLabel(customer.status) }}
                </span>
              </span>
            </div>
            <div class="info-item">
              <label>来源</label>
              <span>{{ getSourceLabel(customer.source) }}</span>
            </div>
            <div class="info-item">
              <label>负责人</label>
              <span>{{ customer.ownerName }}</span>
            </div>
            <div class="info-item">
              <label>创建时间</label>
              <span>{{ formatDateTime(customer.createdAt) }}</span>
            </div>
          </div>

          <div class="info-row" v-if="customer.address">
            <label>地址：</label>
            <span>{{ customer.address }}</span>
          </div>

          <div class="info-row" v-if="customer.remark">
            <label>备注：</label>
            <span>{{ customer.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 跟进记录面板 -->
      <div class="info-card">
        <div class="card-header">
          <h3>📝 跟进记录</h3>
          <div class="card-actions">
            <button class="btn-small" @click="loadFollowups">🔄 刷新</button>
          </div>
        </div>
        <div class="card-body">
          <div v-if="followups.length === 0" class="empty-records">
            <p>暂无跟进记录</p>
            <button class="btn-small-primary" @click="handleAddFollowup">立即添加</button>
          </div>

          <div v-else class="followup-list">
            <div v-for="item in followups" :key="item.id" class="followup-item">
              <div class="followup-header">
                <div class="followup-meta">
                  <span class="type-tag" :class="'type-' + item.type">{{ getFollowupTypeLabel(item.type) }}</span>
                  <span class="followup-time">{{ formatDateTime(item.followTime) }}</span>
                  <span class="followup-user">{{ item.userName }}</span>
                </div>
                <button
                  v-if="item.userId === currentUserId"
                  class="btn-delete-small"
                  @click="deleteFollowup(item.id)"
                >
                  删除
                </button>
              </div>
              <div class="followup-content">{{ item.content }}</div>
              <div v-if="item.nextFollowTime" class="followup-plan">
                ⏰ 下次跟进: {{ formatDate(item.nextFollowTime) }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="info-card" v-if="stats">
        <div class="card-header">
          <h3>📊 跟进统计</h3>
        </div>
        <div class="card-body">
          <div class="stats-grid">
            <div class="stat-box">
              <div class="stat-number">{{ stats.totalFollowups }}</div>
              <div class="stat-label">总跟进次数</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ stats.lastFollowupDays }}</div>
              <div class="stat-label">距离上次(天)</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ stats.followupTypes }}</div>
              <div class="stat-label">跟进方式</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建跟进弹窗 -->
    <div v-if="showFollowupModal" class="modal-overlay" @click="showFollowupModal = false">
      <div class="modal-content" @click.stop>
        <h3>📝 新建跟进记录</h3>
        <form @submit.prevent="submitFollowup" class="followup-form">
          <div class="form-group">
            <label>跟进方式 *</label>
            <select v-model="followupForm.type" required class="form-select">
              <option value="">请选择</option>
              <option value="phone">电话</option>
              <option value="visit">拜访</option>
              <option value="email">邮件</option>
              <option value="wechat">微信</option>
              <option value="meeting">会议</option>
            </select>
          </div>

          <div class="form-group">
            <label>跟进内容 *</label>
            <textarea
              v-model="followupForm.content"
              required
              rows="4"
              placeholder="请输入跟进内容..."
              class="form-textarea"
            ></textarea>
          </div>

          <div class="form-group">
            <label>下次跟进时间</label>
            <input
              type="date"
              v-model="followupForm.nextFollowTime"
              class="form-input"
            />
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="showFollowupModal = false">取消</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCustomerDetailApi } from '../../api/customer'
import { addFollowupApi, getCustomerFollowupsApi, deleteFollowupApi } from '../../api/followup'
import { formatDate, formatDateTime } from '../../utils/format'
import { getCurrentUser } from '../../utils/auth'

const router = useRouter()
const route = useRoute()

const customerId = route.params.id
const loading = ref(false)
const customer = ref(null)
const followups = ref([])
const stats = ref(null)

const currentUserId = ref(null)

// 跟进弹窗
const showFollowupModal = ref(false)
const submitting = ref(false)
const followupForm = ref({
  type: '',
  content: '',
  nextFollowTime: '',
  customerId: ''
})

// 格式化方法
function getStatusLabel(status) {
  const map = {
    'potential': '潜在客户',
    'contacted': '已联系',
    'negotiating': '谈判中',
    'success': '已成交',
    'failed': '已流失'
  }
  return map[status] || status
}

function getSourceLabel(source) {
  const map = {
    'online': '网络咨询',
    'referral': '客户推荐',
    'advertising': '广告投放',
    'offline': '线下活动'
  }
  return map[source] || source
}

function getFollowupTypeLabel(type) {
  const map = {
    'phone': '电话',
    'visit': '拜访',
    'email': '邮件',
    'wechat': '微信',
    'meeting': '会议'
  }
  return map[type] || type
}

// 返回
function handleBack() {
  router.back()
}

// 编辑
function handleEdit() {
  router.push(`/customers/form/${customerId}`)
}

// 加载客户详情
async function loadCustomer() {
  loading.value = true
  try {
    const res = await getCustomerDetailApi(customerId)
    customer.value = res.data

    // 计算统计信息
    if (res.data.followups) {
      followups.value = res.data.followups
      calculateStats()
    }
  } catch (error) {
    console.error('加载客户详情失败:', error)
    alert('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 加载跟进记录（单独方法，用于刷新）
async function loadFollowups() {
  try {
    const res = await getCustomerFollowupsApi(customerId)
    followups.value = res.data
    calculateStats()
  } catch (error) {
    console.error('加载跟进记录失败:', error)
  }
}

// 计算统计信息
function calculateStats() {
  if (!followups.value || followups.value.length === 0) {
    stats.value = {
      totalFollowups: 0,
      lastFollowupDays: 0,
      followupTypes: 0
    }
    return
  }

  const types = new Set(followups.value.map(f => f.type))
  const lastFollowup = followups.value[0] // 按时间倒序

  stats.value = {
    totalFollowups: followups.value.length,
    lastFollowupDays: Math.floor((Date.now() - new Date(lastFollowup.followTime)) / (1000 * 60 * 60 * 24)),
    followupTypes: types.size
  }
}

// 打开新建跟进弹窗
function handleAddFollowup() {
  followupForm.value = {
    type: '',
    content: '',
    nextFollowTime: '',
    customerId: customerId
  }
  showFollowupModal.value = true
}

// 提交跟进
async function submitFollowup() {
  if (!followupForm.value.type || !followupForm.value.content) {
    alert('请填写完整信息')
    return
  }

  submitting.value = true
  try {
    await addFollowupApi(followupForm.value)
    alert('跟进记录添加成功')
    showFollowupModal.value = false
    loadFollowups() // 刷新跟进记录
  } catch (error) {
    console.error('提交跟进失败:', error)
    alert('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 删除跟进
async function deleteFollowup(id) {
  if (!confirm('确定要删除这条跟进记录吗？')) return

  try {
    await deleteFollowupApi(id)
    alert('删除成功')
    loadFollowups() // 刷新列表
  } catch (error) {
    console.error('删除跟进失败:', error)
    alert('删除失败')
  }
}

onMounted(() => {
  const user = getCurrentUser()
  if (user) {
    currentUserId.value = user.id
  }
  loadCustomer()
})
</script>

<style scoped>
/* 页面头部 */
.page-header {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 22px;
}

.header-right {
  display: flex;
  gap: 8px;
}

.btn-back {
  background: #ecf0f1;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}

.btn-back:hover {
  background: #e0e0e0;
}

.btn-edit {
  background: #f39c12;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.btn-edit:hover {
  background: #e67e22;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: transform 0.2s;
}

.btn-primary:hover {
  transform: translateY(-1px);
}

.btn-small {
  background: #ecf0f1;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
}

.btn-small:hover {
  background: #e0e0e0;
}

.btn-small-primary {
  background: #667eea;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 16px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.card-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-body {
  padding: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
  text-transform: uppercase;
}

.info-item span {
  color: #2c3e50;
  font-size: 14px;
  font-weight: 500;
}

.info-row {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}

.info-row label {
  min-width: 80px;
  color: #999;
  font-weight: 500;
  font-size: 14px;
}

.info-row span {
  flex: 1;
  color: #2c3e50;
  font-size: 14px;
}

/* 状态标签和类型标签 */
.status-badge, .type-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  color: white;
}

.status-potential { background: #3498db; }
.status-contacted { background: #9b59b6; }
.status-negotiating { background: #e67e22; }
.status-success { background: #27ae60; }
.status-failed { background: #e74c3c; }

.type-tag {
  font-size: 10px;
  padding: 3px 8px;
}

.type-phone { background: #3498db; }
.type-visit { background: #27ae60; }
.type-email { background: #9b59b6; }
.type-wechat { background: #2ecc71; }
.type-meeting { background: #e67e22; }

/* 跟进记录列表 */
.followup-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.followup-item {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 14px;
  background: #fafafa;
  transition: background 0.2s;
}

.followup-item:hover {
  background: #f5f5f5;
}

.followup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.followup-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.followup-time {
  color: #999;
  font-size: 12px;
}

.followup-user {
  color: #666;
  font-size: 12px;
  font-weight: 500;
}

.followup-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 6px;
}

.followup-plan {
  color: #e67e22;
  font-size: 12px;
  font-weight: 500;
}

.btn-delete-small {
  background: transparent;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  font-weight: 500;
}

.btn-delete-small:hover {
  background: #e74c3c;
  color: white;
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  text-align: center;
}

.stat-box {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

/* 空状态 */
.empty-records {
  text-align: center;
  padding: 30px 20px;
  color: #999;
}

.empty-records p {
  margin-bottom: 16px;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-content h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.followup-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 表单样式 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.form-select, .form-input, .form-textarea {
  padding: 10px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
}

.form-select:focus, .form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.btn-secondary {
  background: #e0e0e0;
  color: #333;
  border: none;
  padding: 10px 18px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.btn-secondary:hover {
  background: #d0d0d0;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
