<template>
  <div class="customer-form">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <button class="btn-back" @click="handleBack">← 返回</button>
        <h2>{{ isEditMode ? '编辑客户' : '新增客户' }}</h2>
      </div>
    </div>

    <!-- 表单区域 -->
    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="customer-form-body">
        <div class="form-section">
          <h3>基本信息</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>客户名称 <span class="required">*</span></label>
              <input
                type="text"
                v-model="form.name"
                required
                placeholder="请输入客户名称"
                class="form-input"
                maxlength="50"
              />
            </div>

            <div class="form-group">
              <label>手机号</label>
              <input
                type="tel"
                v-model="form.phone"
                placeholder="请输入手机号"
                class="form-input"
                maxlength="11"
                pattern="[0-9]{11}"
                title="请输入11位手机号"
              />
            </div>

            <div class="form-group">
              <label>邮箱</label>
              <input
                type="email"
                v-model="form.email"
                placeholder="请输入邮箱地址"
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label>公司名称</label>
              <input
                type="text"
                v-model="form.company"
                placeholder="请输入公司名称"
                class="form-input"
              />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3>详细信息</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>客户状态 <span class="required">*</span></label>
              <select v-model="form.status" required class="form-input">
                <option value="">请选择状态</option>
                <option value="1">正常</option>
                <option value="2">已成交</option>
                <option value="3">已流失</option>
              </select>
            </div>

            <div class="form-group">
              <label>客户来源 <span class="required">*</span></label>
              <select v-model="form.source" required class="form-input">
                <option value="">请选择来源</option>
                <option value="online">网络咨询</option>
                <option value="referral">客户推荐</option>
                <option value="advertising">广告投放</option>
                <option value="offline">线下活动</option>
              </select>
            </div>

            <div class="form-group" v-if="isEditMode && isManagerOrAdmin">
              <label>负责人</label>
              <select v-model="form.ownerId" class="form-input">
                <option v-for="user in availableOwners" :key="user.id" :value="user.id">
                  {{ user.nickname }} ({{ user.username }})
                </option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>地址</label>
            <input
              type="text"
              v-model="form.address"
              placeholder="请输入详细地址"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label>备注</label>
            <textarea
              v-model="form.remark"
              rows="4"
              placeholder="请输入备注信息（如客户需求、意向程度等）"
              class="form-textarea"
              maxlength="500"
            ></textarea>
            <div class="char-count">{{ form.remark.length }}/500</div>
          </div>
        </div>

        <!-- 表单操作 -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="handleBack">
            取消
          </button>
          <button type="submit" class="btn-submit" :disabled="submitting">
            {{ submitting ? '提交中...' : (isEditMode ? '保存修改' : '创建客户') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  getCustomerDetailApi,
  createCustomerApi,
  updateCustomerApi,
  getAvailableOwnersApi
} from '../../api/customer'
import { getCurrentUser, isManager, isAdmin } from '../../utils/auth'

const router = useRouter()
const route = useRoute()

const customerId = route.params.id
const isEditMode = !!customerId
const submitting = ref(false)

// 判断当前用户角色是否经理或管理员
const isManagerOrAdmin = computed(() => isManager() || isAdmin())

const availableOwners = ref([])

// 表单数据
const form = ref({
  name: '',
  phone: '',
  email: '',
  company: '',
  status: '',
  source: '',
  address: '',
  remark: '',
  ownerId: ''
})

// 返回
function handleBack() {
  router.back()
}

// 加载客户详情（编辑模式）
async function loadCustomer() {
  if (!isEditMode) return

  try {
    const res = await getCustomerDetailApi(customerId)
    const data = res.data

    // 填充表单
    form.value = {
      name: data.name || '',
      phone: data.phone || '',
      email: data.email || '',
      company: data.company || '',
      status: data.status || '',
      source: data.source || '',
      address: data.address || '',
      remark: data.remark || '',
      ownerId: data.ownerId || ''
    }
  } catch (error) {
    console.error('加载客户数据失败:', error)
    alert('加载失败')
    router.back()
  }
}

// 加载负责人列表（经理/管理员可见）
async function loadAvailableOwners() {
  if (!isManagerOrAdmin.value) return

  try {
    const res = await getAvailableOwnersApi()
    availableOwners.value = res.data
  } catch (error) {
    console.error('加载负责人列表失败:', error)
  }
}

// 验证表单
function validateForm() {
  if (!form.value.name || form.value.name.length < 2) {
    alert('客户名称至少需要2个字符')
    return false
  }

  // 手机号验证（如果填写）
  if (form.value.phone && !/^1[3-9]\d{9}$/.test(form.value.phone)) {
    alert('请输入有效的11位手机号')
    return false
  }

  // 邮箱验证（如果填写）
  if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    alert('请输入有效的邮箱地址')
    return false
  }

  if (!form.value.status) {
    alert('请选择客户状态')
    return false
  }

  if (!form.value.source) {
    alert('请选择客户来源')
    return false
  }

  return true
}

// 提交表单
async function handleSubmit() {
  if (!validateForm()) return

  submitting.value = true
  try {
    const submitData = { ...form.value }

    // 如果是普通用户，自动设置负责人为当前用户
    if (!isManagerOrAdmin.value) {
      const currentUser = getCurrentUser()
      submitData.ownerId = currentUser.id
    }

    if (isEditMode) {
      await updateCustomerApi(customerId, submitData)
      alert('客户信息已更新')
    } else {
      await createCustomerApi(submitData)
      alert('客户创建成功')
    }

    router.back()
  } catch (error) {
    console.error(isEditMode ? '更新失败:' : '创建失败:', error)
    alert(error.message || (isEditMode ? '更新失败，请重试' : '创建失败，请重试'))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEditMode) {
    loadCustomer()
  }
  if (isManagerOrAdmin.value) {
    loadAvailableOwners()
  }
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

/* 表单容器 */
.form-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 24px;
}

.form-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.form-section h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

/* 表单网格布局 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

/* 表单组 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
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

.form-input, .form-textarea {
  padding: 10px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
  font-family: inherit;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: 667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input:hover, .form-textarea:hover {
  border-color: #d0d0d0;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.char-count {
  position: absolute;
  bottom: 4px;
  right: 12px;
  font-size: 11px;
  color: #999;
}

/* 表单操作区 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid #f0f0f0;
}

.btn-cancel {
  background: #ecf0f1;
  color: #333;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 选项提示 */
.input-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
