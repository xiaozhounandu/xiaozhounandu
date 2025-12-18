<template>
  <div class="followup-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>📝 跟进记录</h2>
        <p class="subtitle">管理所有客户的跟进记录</p>
      </div>
      <div class="header-right">
        <button class="btn-primary" @click="handleQuickAdd">
          <span>+</span> 快速记录
        </button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <div class="search-group">
        <input
          type="text"
          v-model="searchParams.keyword"
          placeholder="搜索客户名称或跟进内容"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="btn-search" @click="handleSearch">搜索</button>
      </div>

      <div class="filter-group">
        <select v-model="searchParams.type" @change="handleSearch" class="filter-select">
          <option value="">全部类型</option>
          <option value="phone">电话</option>
          <option value="visit">拜访</option>
          <option value="email">邮件</option>
          <option value="wechat">微信</option>
          <option value="meeting">会议</option>
        </select>

        <select v-model="searchParams.userId" @change="handleSearch" class="filter-select" v-if="isManagerOrAdmin">
          <option value="">所有人</option>
          <option v-for="user in users" :key="user.id" :value="user.id">
            {{ user.nickname }}
          </option>
        </select>

        <input
          type="date"
          v-model="searchParams.startDate"
          @change="handleSearch"
          class="filter-date"
          placeholder="开始日期"
        />
        <input
          type="date"
          v-model="searchParams.endDate"
          @change="handleSearch"
          class="filter-date"
          placeholder="结束日期"
        />
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container" v-loading="loading">
      <table class="data-table" v-if="followups.length > 0">
        <thead>
          <tr>
            <th>客户名称</th>
            <th>类型</th>
            <th>内容摘要</th>
            <th>跟进人</th>
            <th>跟进时间</th>
            <th>下次跟进</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in followups" :key="item.id">
            <td>
              <span
                class="customer-link"
                @click="goToCustomer(item.customerId)"
              >
                {{ item.customerName }}
              </span>
            </td>
            <td>
              <span class="type-badge" :class="'type-' + item.type">
                {{ getFollowupTypeLabel(item.type) }}
              </span>
            </td>
            <td>
              <div class="content-text">{{ item.content }}</div>
            </td>
            <td>{{ item.userName }}</td>
            <td>{{ formatDateTime(item.followTime) }}</td>
            <td>
              <span v-if="item.nextFollowTime" :class="getDueClass(item.nextFollowTime)">
                {{ formatDate(item.nextFollowTime) }}
              </span>
              <span v-else style="color: #999;">-</span>
            </td>
            <td>
              <div class="action-buttons">
                <button class="btn-icon" @click="handleDetail(item.customerId)" title="查看客户">👁️</button>
                <button
                  v-if="item.userId === currentUserId"
                  class="btn-icon"
                  @click="handleEdit(item.id)"
                  title="编辑"
                >
                  ✏️
                </button>
                <button
                  v-if="item.userId === currentUserId"
                  class="btn-icon btn-delete"
                  @click="handleDelete(item.id)"
                  title="删除"
                >
                  🗑️
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无跟进记录</p>
        <button class="btn-primary" @click="handleQuickAdd">创建第一条记录</button>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container" v-if="total > 0">
      <div class="pagination-info">
        共 {{ total }} 条记录，第 {{ currentPage }} / {{ totalPages }} 页
      </div>
      <div class="pagination-buttons">
        <button
          class="btn-page"
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <button
          v-for="page in displayPages"
          :key="page"
          class="btn-page"
          :class="{ active: currentPage === page }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
        <button
          class="btn-page"
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 快速记录弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click="showModal = false">
      <div class="modal-content" @click.stop>
        <h3>📝 快速记录</h3>
        <form @submit.prevent="submitQuickAdd" class="modal-form">
          <div class="form-group">
            <label>选择客户 <span class="required">*</span></label>
            <select v-model="form.customerId" required class="form-select">
              <option value="">请选择客户</option>
              <option
                v-for="customer in quickCustomers"
                :key="customer.id"
                :value="customer.id"
              >
                {{ customer.name }} ({{ customer.phone }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>跟进方式 <span class="required">*</span></label>
            <select v-model="form.type" required class="form-select">
              <option value="">请选择</option>
              <option value="phone">电话</option>
              <option value="visit">拜访</option>
              <option value="email">邮件</option>
              <option value="wechat">微信</option>
              <option value="meeting">会议</option>
            </select>
          </div>

          <div class="form-group">
            <label>跟进内容 <span class="required">*</span></label>
            <textarea
              v-model="form.content"
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
              v-model="form.nextFollowTime"
              class="form-input"
            />
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="showModal = false">取消</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click="showEditModal = false">
      <div class="modal-content" @click.stop>
        <h3>✏️ 编辑跟进</h3>
        <form @submit.prevent="submitEdit" class="modal-form">
          <div class="form-group">
            <label>跟进方式 <span class="required">*</span></label>
            <select v-model="editForm.type" required class="form-select">
              <option value="">请选择</option>
              <option value="phone">电话</option>
              <option value="visit">拜访</option>
              <option value="email">邮件</option>
              <option value="wechat">微信</option>
              <option value="meeting">会议</option>
            </select>
          </div>

          <div class="form-group">
            <label>跟进内容 <span class="required">*</span></label>
            <textarea
              v-model="editForm.content"
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
              v-model="editForm.nextFollowTime"
              class="form-input"
            />
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="showEditModal = false">取消</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getFollowupsApi,
  deleteFollowupApi,
  updateFollowupApi,
  addFollowupApi
} from '../../api/followup'
import { getCustomersApi } from '../../api/customer'
import { getSimpleUsersApi } from '../../api/auth'
import { formatDate, formatDateTime } from '../../utils/format'
import { getCurrentUser, isManager, isAdmin } from '../../utils/auth'

const router = useRouter()

// 数据状态
const followups = ref([])
const quickCustomers = ref([])
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const submitting = ref(false)

// 当前用户信息
const currentUser = getCurrentUser()
const currentUserId = ref(currentUser?.id)
const isManagerOrAdmin = computed(() => isManager() || isAdmin())

// 搜索参数
const searchParams = ref({
  keyword: '',
  type: '',
  userId: '',
  startDate: '',
  endDate: ''
})

// 弹窗控制
const showModal = ref(false)
const showEditModal = ref(false)

// 表单数据
const form = ref({
  customerId: '',
  type: '',
  content: '',
  nextFollowTime: ''
})

const editForm = ref({
  id: '',
  type: '',
  content: '',
  nextFollowTime: ''
})

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const displayPages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 获取跟进类型标签
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

// 获取到期样式
function getDueClass(date) {
  const now = new Date()
  const dueDate = new Date(date)
  const diffDays = Math.ceil((dueDate - now) / (1000 * 60 * 60 * 24))

  if (diffDays < 0) return 'overdue'
  if (diffDays <= 3) return 'urgent'
  return ''
}

// 加载我的跟进记录
async function loadFollowups() {
  loading.value = true
  try {
    // 普通用户只能看自己的，管理员/经理可以看所有或指定用户
    let userId = currentUser?.id
    if (isManagerOrAdmin.value && searchParams.value.userId) {
      userId = searchParams.value.userId
    }

    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchParams.value.keyword,
      type: searchParams.value.type,
      userId: userId,
      startDate: searchParams.value.startDate,
      endDate: searchParams.value.endDate
    }

    const res = await getFollowupsApi(params)
    followups.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载跟进记录失败:', error)
    alert('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 加载客户列表（用于快速记录）
async function loadQuickCustomers() {
  try {
    const res = await getCustomersApi({ page: 1, size: 1000 })
    quickCustomers.value = res.data.list
  } catch (error) {
    console.error('加载客户列表失败:', error)
  }
}

// 加载用户列表（用于筛选）
async function loadUsers() {
  if (!isManagerOrAdmin.value) return
  try {
    const res = await getSimpleUsersApi()
    users.value = res.data
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadFollowups()
}

// 分页切换
function changePage(page) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadFollowups()
}

// 快速添加
function handleQuickAdd() {
  form.value = {
    customerId: '',
    type: '',
    content: '',
    nextFollowTime: ''
  }
  showModal.value = true
}

// 提交快速添加
async function submitQuickAdd() {
  if (!form.value.customerId || !form.value.type || !form.value.content) {
    alert('请填写必填项')
    return
  }

  submitting.value = true
  try {
    await addFollowupApi(form.value)
    alert('跟进记录添加成功')
    showModal.value = false
    loadFollowups() // 刷新列表
  } catch (error) {
    console.error('添加失败:', error)
    alert('添加失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 编辑
async function handleEdit(id) {
  try {
    const res = await getFollowupsApi({ id })
    const item = res.data.list[0]
    if (!item) {
      alert('记录不存在')
      return
    }

    editForm.value = {
      id: item.id,
      type: item.type,
      content: item.content,
      nextFollowTime: item.nextFollowTime ? formatDate(item.nextFollowTime) : ''
    }
    showEditModal.value = true
  } catch (error) {
    console.error('加载记录失败:', error)
  }
}

// 提交编辑
async function submitEdit() {
  if (!editForm.value.type || !editForm.value.content) {
    alert('请填写必填项')
    return
  }

  submitting.value = true
  try {
    await updateFollowupApi(editForm.value.id, editForm.value)
    alert('保存成功')
    showEditModal.value = false
    loadFollowups()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 删除
async function handleDelete(id) {
  if (!confirm('确定要删除这条跟进记录吗？')) return

  try {
    await deleteFollowupApi(id)
    alert('删除成功')
    loadFollowups()
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}

// 跳转到客户详情
function handleDetail(customerId) {
  router.push(`/customers/detail/${customerId}`)
}

// 跳转到客户
function goToCustomer(customerId) {
  router.push(`/customers/detail/${customerId}`)
}

onMounted(() => {
  loadFollowups()
  loadQuickCustomers()
  loadUsers()
})
</script>

<style scoped>
/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-left h2 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 24px;
}

.subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.2s, box-shadow 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 搜索和筛选栏 */
.filter-bar {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-group {
  display: flex;
  gap: 8px;
}

.search-input {
  flex: 1;
  padding: 10px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-search {
  background: #667eea;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-search:hover {
  background: #5568d3;
}

.filter-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-select, .filter-date {
  padding: 8px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background: white;
}

.filter-date {
  max-width: 160px;
}

.filter-select:focus, .filter-date:focus {
  outline: none;
  border-color: #667eea;
}

/* 表格容器 */
.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  min-height: 300px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8f9fa;
  padding: 14px 12px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e9ecef;
  font-size: 14px;
}

.data-table td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  color: #555;
  font-size: 14px;
  vertical-align: top;
}

.data-table tr:hover {
  background: #f8f9fa;
}

/* 客户链接 */
.customer-link {
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s;
}

.customer-link:hover {
  color: #5568d3;
  text-decoration: underline;
}

/* 类型标签 */
.type-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  color: white;
}

.type-phone { background: #3498db; }
.type-visit { background: #27ae60; }
.type-email { background: #9b59b6; }
.type-wechat { background: #2ecc71; }
.type-meeting { background: #e67e22; }

/* 内容文本 */
.content-text {
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.6;
}

/* 下次跟进时间样式 */
.overdue {
  color: #e74c3c;
  font-weight: 600;
}

.urgent {
  color: #e67e22;
  font-weight: 600;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 6px;
}

.btn-icon {
  background: transparent;
  border: none;
  padding: 6px 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
  font-size: 14px;
}

.btn-icon:hover {
  background: #e9ecef;
}

.btn-delete:hover {
  background: #fee;
  color: #e74c3c;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 16px;
}

/* 分页 */
.pagination-container {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-buttons {
  display: flex;
  gap: 6px;
}

.btn-page {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  min-width: 36px;
  transition: all 0.2s;
}

.btn-page:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #667eea;
}

.btn-page.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.modal-form {
  display: flex;
  flex-direction: column;
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
</style>
