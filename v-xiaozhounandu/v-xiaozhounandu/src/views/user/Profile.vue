<template>
  <div class="profile">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>👤 个人中心</h2>
      <p class="subtitle">管理您的个人信息和设置</p>
    </div>

    <div class="profile-content">
      <!-- 基本信息卡 -->
      <div class="info-card">
        <div class="card-header">
          <h3>📋 基本信息</h3>
        </div>
        <div class="card-body" v-if="user">
          <div class="info-grid">
            <div class="info-item">
              <label>用户名</label>
              <span>{{ user.username }}</span>
            </div>
            <div class="info-item">
              <label>真实姓名</label>
              <span>{{ user.realName || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>昵称</label>
              <span>{{ user.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>角色</label>
              <span>
                <span class="role-badge" :class="user.role.toLowerCase()">
                  {{ getRoleLabel(user.role) }}
                </span>
              </span>
            </div>
            <div class="info-item">
              <label>邮箱</label>
              <span>{{ user.email || '未绑定' }}</span>
            </div>
            <div class="info-item">
              <label>手机号</label>
              <span>{{ user.phone || '未绑定' }}</span>
            </div>
            <div class="info-item">
              <label>注册时间</label>
              <span>{{ formatDate(user.createdAt) }}</span>
            </div>
            <div class="info-item">
              <label>最后登录</label>
              <span>{{ formatDateTime(user.lastLoginTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 修改个人信息 -->
      <div class="info-card">
        <div class="card-header">
          <h3>✏️ 修改个人信息</h3>
        </div>
        <div class="card-body">
          <form @submit.prevent="updateProfile" class="profile-form">
            <div class="form-grid">
              <div class="form-group">
                <label>真实姓名</label>
                <input
                  type="text"
                  v-model="profileForm.realName"
                  placeholder="请输入真实姓名"
                  class="form-input"
                />
              </div>

              <div class="form-group">
                <label>昵称</label>
                <input
                  type="text"
                  v-model="profileForm.nickname"
                  placeholder="请输入昵称"
                  class="form-input"
                />
              </div>

              <div class="form-group">
                <label>邮箱</label>
                <input
                  type="email"
                  v-model="profileForm.email"
                  placeholder="请输入邮箱"
                  class="form-input"
                />
              </div>

              <div class="form-group">
                <label>手机号</label>
                <input
                  type="tel"
                  v-model="profileForm.phone"
                  placeholder="请输入手机号"
                  class="form-input"
                  maxlength="11"
                />
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn-primary" :disabled="submitting">
                {{ submitting ? '保存中...' : '保存修改' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 修改密码 -->
      <div class="info-card">
        <div class="card-header">
          <h3>🔒 修改密码</h3>
        </div>
        <div class="card-body">
          <form @submit.prevent="changePassword" class="password-form">
            <div class="form-grid">
              <div class="form-group">
                <label>当前密码 <span class="required">*</span></label>
                <input
                  type="password"
                  v-model="pwdForm.currentPassword"
                  placeholder="请输入当前密码"
                  class="form-input"
                  required
                />
              </div>

              <div class="form-group">
                <label>新密码 <span class="required">*</span></label>
                <input
                  type="password"
                  v-model="pwdForm.newPassword"
                  placeholder="请输入新密码（至少6位）"
                  class="form-input"
                  required
                  minlength="6"
                />
              </div>

              <div class="form-group">
                <label>确认新密码 <span class="required">*</span></label>
                <input
                  type="password"
                  v-model="pwdForm.confirmPassword"
                  placeholder="请再次输入新密码"
                  class="form-input"
                  required
                />
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn-primary" :disabled="submittingPwd">
                {{ submittingPwd ? '修改中...' : '修改密码' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="info-card" v-if="stats">
        <div class="card-header">
          <h3>📊 个人统计</h3>
        </div>
        <div class="card-body">
          <div class="stats-grid">
            <div class="stat-box">
              <div class="stat-number">{{ stats.customers }}</div>
              <div class="stat-label">负责客户数</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ stats.followups }}</div>
              <div class="stat-label">跟进记录</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ stats.todayFollowups }}</div>
              <div class="stat-label">今日跟进</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ stats.successRate }}%</div>
              <div class="stat-label">成交率</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { updateProfileApi, changePasswordApi, getStatsApi } from '../../api/auth'
import { getCurrentUser, logout } from '../../utils/auth'
import { formatDate, formatDateTime } from '../../utils/format'

const router = useRouter()

const user = ref(null)
const stats = ref(null)
const submitting = ref(false)
const submittingPwd = ref(false)

// 个人信息表单
const profileForm = ref({
  realName: '',
  nickname: '',
  email: '',
  phone: ''
})

// 密码修改表单
const pwdForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 获取角色标签
function getRoleLabel(role) {
  const map = {
    'ADMIN': '管理员',
    'MANAGER': '销售经理',
    'USER': '用户'
  }
  return map[role] || role
}

// 加载用户信息
function loadUser() {
  const currentUser = getCurrentUser()
  if (currentUser) {
    user.value = currentUser
    // 填充表单
    profileForm.value = {
      realName: currentUser.realName || '',
      nickname: currentUser.nickname || '',
      email: currentUser.email || '',
      phone: currentUser.phone || ''
    }
  }
}

// 加载统计信息
async function loadStats() {
  try {
    const res = await getStatsApi()
    stats.value = res.data
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 更新个人信息
async function updateProfile() {
  submitting.value = true
  try {
    const res = await updateProfileApi(profileForm.value)

    // 更新本地存储的用户信息
    const updatedUser = { ...user.value, ...profileForm.value }
    localStorage.setItem('user', JSON.stringify(updatedUser))
    user.value = updatedUser

    alert('个人信息已更新')
  } catch (error) {
    console.error('更新失败:', error)
    alert('更新失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 修改密码
async function changePassword() {
  const { currentPassword, newPassword, confirmPassword } = pwdForm.value

  if (!currentPassword || !newPassword || !confirmPassword) {
    alert('请填写所有必填项')
    return
  }

  if (newPassword.length < 6) {
    alert('新密码至少需要6位字符')
    return
  }

  if (newPassword !== confirmPassword) {
    alert('两次输入的新密码不一致')
    return
  }

  if (newPassword === currentPassword) {
    alert('新密码不能与当前密码相同')
    return
  }

  submittingPwd.value = true
  try {
    await changePasswordApi({
      currentPassword,
      newPassword
    })

    alert('密码修改成功！请重新登录')
    await logout()
    router.push('/login')
  } catch (error) {
    console.error('修改密码失败:', error)
    alert(error.message || '修改密码失败，请检查当前密码是否正确')
  } finally {
    submittingPwd.value = false
  }
}

onMounted(() => {
  loadUser()
  loadStats()
})
</script>

<style scoped>
/* 页面头部 */
.page-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 24px;
}

.subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
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
  font-weight: 600;
}

.card-body {
  padding: 20px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
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

/* 角色标签 */
.role-badge {
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.admin {
  background: #fef0f0;
  color: #f56c6c;
}

.manager {
  background: #fdf6ec;
  color: #e6a23c;
}

.user {
  background: #eef2f7;
  color: #606266;
}

/* 表单 */
.profile-form,
.password-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

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

.required {
  color: #e74c3c;
  font-weight: bold;
}

.form-input {
  padding: 10px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input:hover {
  border-color: #d0d0d0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.stat-box {
  text-align: center;
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

/* 响应式 */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
