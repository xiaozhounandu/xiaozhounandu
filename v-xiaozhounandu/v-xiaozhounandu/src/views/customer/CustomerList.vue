<template>
  <div class="customer-list">
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2>👥 客户管理</h2>
        <p class="subtitle">管理和维护客户信息</p>
      </div>
      <div class="header-right">
        <button class="btn-primary" @click="handleAdd">
          <span>+</span> 新增客户
        </button>
      </div>
    </div>

    <!-- 搜索和筛选栏 -->
    <div class="filter-bar">
      <div class="search-group">
        <input
          type="text"
          v-model="searchParams.keyword"
          placeholder="搜索客户名称/联系方式"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="btn-search" @click="handleSearch">搜索</button>
      </div>

      <div class="filter-group">
        <select v-model="searchParams.status" @change="handleSearch" class="filter-select">
          <option value="">全部状态</option>
          <option value="potential">潜在客户</option>
          <option value="contacted">已联系</option>
          <option value="negotiating">谈判中</option>
          <option value="success">已成交</option>
          <option value="failed">已流失</option>
        </select>

        <select v-model="searchParams.source" @change="handleSearch" class="filter-select">
          <option value="">全部来源</option>
          <option value="online">网络咨询</option>
          <option value="referral">客户推荐</option>
          <option value="advertising">广告投放</option>
          <option value="offline">线下活动</option>
        </select>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container" v-loading="loading">
      <table class="data-table" v-if="customers.length > 0">
        <thead>
          <tr>
            <th>客户名称</th>
            <th>联系方式</th>
            <th>状态</th>
            <th>来源</th>
            <th>负责人</th>
            <th>最后跟进</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in customers" :key="item.id">
            <td>
              <span class="customer-name" @click="handleDetail(item.id)">{{ item.name }}</span>
            </td>
            <td>
              <div>{{ item.phone }}</div>
              <div style="color: #999; font-size: 12px;">{{ item.email }}</div>
            </td>
            <td>
              <span class="status-badge" :class="'status-' + item.status">
                {{ getStatusLabel(item.status) }}
              </span>
            </td>
            <td>
              <span class="source-tag">{{ getSourceLabel(item.source) }}</span>
            </td>
            <td>{{ item.ownerName }}</td>
            <td>{{ item.lastFollowupTime ? formatDateTime(item.lastFollowupTime) : '暂无' }}</td>
            <td>{{ formatDate(item.createdAt) }}</td>
            <td>
              <div class="action-buttons">
                <button class="btn-icon" @click="handleDetail(item.id)" title="查看详情">👁️</button>
                <button class="btn-icon" @click="handleEdit(item.id)" title="编辑">✏️</button>
                <button class="btn-icon" @click="handleTransfer(item.id)" title="转移">👥</button>
                <button class="btn-icon btn-delete" @click="handleDelete(item.id)" title="删除">🗑️</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无客户数据</p>
        <button class="btn-primary" @click="handleAdd">创建第一个客户</button>
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

    <!-- 转移客户弹窗 -->
    <div v-if="showTransferModal" class="modal-overlay" @click="showTransferModal = false">
      <div class="modal-content" @click.stop>
        <h3>转移客户</h3>
        <div class="form-group">
          <label>选择新负责人</label>
          <select v-model="transferTargetId" class="modal-select">
            <option value="">请选择</option>
            <option
              v-for="user in availableOwners"
              :key="user.id"
              :value="user.id"
            >
              {{ user.nickname }} ({{ user.username }})
            </option>
          </select>
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showTransferModal = false">取消</button>
          <button class="btn-primary" @click="confirmTransfer" :disabled="!transferTargetId">
            确认转移
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCustomersApi, deleteCustomerApi, transferCustomerApi, getAvailableOwnersApi } from '../../api/customer'
import { formatDate, formatDateTime } from '../../utils/format'

const router = useRouter()

// 数据状态
const customers = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 搜索参数
const searchParams = ref({
  keyword: '',
  status: '',
  source: ''
})

// 转移相关
const showTransferModal = ref(false)
const transferCustomerId = ref('')
const transferTargetId = ref('')
const availableOwners = ref([])

// 计算总页数
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 计算显示的页码（简单版本，显示前后2页）
const displayPages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 获取状态标签
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

// 获取来源标签
function getSourceLabel(source) {
  const map = {
    'online': '网络咨询',
    'referral': '客户推荐',
    'advertising': '广告投放',
    'offline': '线下活动'
  }
  return map[source] || source
}

// 加载客户列表
async function loadCustomers() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchParams.value
    }
    console.log('开始加载客户列表，参数:', params)
    const res = await getCustomersApi(params)
    console.log('API响应:', res)
    customers.value = res.data.list
    total.value = res.data.total
    console.log('客户数据:', customers.value, '总数:', total.value)
  } catch (error) {
    console.error('加载客户列表失败:', error)
    alert('加载失败: ' + (error.response?.data?.message || error.message || '请检查网络和后端服务'))
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadCustomers()
}

// 分页切换
function changePage(page) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadCustomers()
}

// 新增客户
function handleAdd() {
  router.push('/customers/form')
}

// 编辑客户
function handleEdit(id) {
  router.push(`/customers/form/${id}`)
}

// 查看详情
function handleDetail(id) {
  router.push(`/customers/detail/${id}`)
}

// 删除客户
async function handleDelete(id) {
  if (!confirm('确定要删除这个客户吗？删除后无法恢复。')) return

  try {
    await deleteCustomerApi(id)
    alert('删除成功')
    loadCustomers() // 刷新列表
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

// 打开转移弹窗
async function handleTransfer(id) {
  transferCustomerId.value = id
  showTransferModal.value = true
  transferTargetId.value = ''

  // 加载可转移的负责人列表
  try {
    const res = await getAvailableOwnersApi()
    availableOwners.value = res.data
  } catch (error) {
    console.error('加载负责人列表失败:', error)
  }
}

// 确认转移
async function confirmTransfer() {
  if (!transferTargetId.value) {
    alert('请选择新负责人')
    return
  }

  try {
    await transferCustomerApi({
      customerId: transferCustomerId.value,
      targetUserId: transferTargetId.value
    })
    alert('转移成功')
    showTransferModal.value = false
    loadCustomers() // 刷新列表
  } catch (error) {
    console.error('转移失败:', error)
    alert('转移失败，请重试')
  }
}

onMounted(() => {
  console.log('CustomerList组件挂载，开始加载数据...')
  loadCustomers()
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

/* 按钮样式 */
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

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 搜索和筛选栏 */
.filter-bar {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.search-group {
  flex: 1;
  min-width: 300px;
  display: flex;
  gap: 8px;
}

.search-input {
  flex: 1;
  padding: 10px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
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
}

.filter-select {
  padding: 10px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background: white;
}

.filter-select:focus {
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
}

.data-table tr:hover {
  background: #f8f9fa;
}

.customer-name {
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s;
}

.customer-name:hover {
  color: #5568d3;
  text-decoration: underline;
}

/* 状态和来源标签 */
.status-badge {
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

.source-tag {
  background: #ecf0f1;
  color: #555;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
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
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-content h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.modal-select {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  margin-top: 8px;
}

.modal-select:focus {
  outline: none;
  border-color: #667eea;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-secondary {
  background: #e0e0e0;
  color: #333;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.btn-secondary:hover {
  background: #d0d0d0;
}

/* 表单组 */
.form-group label {
  display: block;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
  font-size: 14px;
}
</style>
