<template>
  <div class="operation-log">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>📋 操作日志</h2>
      <p class="subtitle">系统所有用户操作记录</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="search-group">
        <input
          type="text"
          v-model="searchParams.keyword"
          placeholder="搜索用户名或操作内容"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="btn-search" @click="handleSearch">搜索</button>
      </div>

      <div class="filter-group">
        <select v-model="searchParams.operation" @change="handleSearch" class="filter-select">
          <option value="">全部操作</option>
          <option value="create">创建</option>
          <option value="update">更新</option>
          <option value="delete">删除</option>
          <option value="login">登录</option>
          <option value="logout">登出</option>
          <option value="transfer">转移</option>
        </select>

        <select v-model="searchParams.module" @change="handleSearch" class="filter-select">
          <option value="">全部模块</option>
          <option value="customer">客户管理</option>
          <option value="followup">跟进记录</option>
          <option value="user">用户管理</option>
          <option value="system">系统管理</option>
        </select>

        <input
          type="date"
          v-model="searchParams.startDate"
          @change="handleSearch"
          class="filter-date"
        />
        <span style="color: #666;">至</span>
        <input
          type="date"
          v-model="searchParams.endDate"
          @change="handleSearch"
          class="filter-date"
        />
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container" v-loading="loading">
      <table class="data-table" v-if="logs.length > 0">
        <thead>
          <tr>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>操作模块</th>
            <th>操作类型</th>
            <th>操作内容</th>
            <th>IP地址</th>
            <th>操作时间</th>
            <th>结果</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in logs" :key="item.id">
            <td>{{ item.username }}</td>
            <td>{{ item.realName || '-' }}</td>
            <td>
              <span class="module-badge" :class="'module-' + item.module">
                {{ getModuleLabel(item.module) }}
              </span>
            </td>
            <td>
              <span class="action-badge" :class="'action-' + item.operation">
                {{ getOperationLabel(item.operation) }}
              </span>
            </td>
            <td class="details-text">{{ item.details }}</td>
            <td>{{ item.ipAddress }}</td>
            <td>{{ formatDateTime(item.createdAt) }}</td>
            <td>
              <span class="result-badge" :class="item.success ? 'success' : 'failed'">
                {{ item.success ? '成功' : '失败' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无操作日志</p>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOperationLogsApi } from '../../api/log'
import { formatDateTime } from '../../utils/format'

// 数据状态
const logs = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const loading = ref(false)

// 搜索参数
const searchParams = ref({
  keyword: '',
  operation: '',
  module: '',
  startDate: '',
  endDate: ''
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

// 获取模块标签
function getModuleLabel(module) {
  const map = {
    'customer': '客户管理',
    'followup': '跟进记录',
    'user': '用户管理',
    'system': '系统管理'
  }
  return map[module] || module
}

// 获取操作类型标签
function getOperationLabel(operation) {
  const map = {
    'create': '创建',
    'update': '更新',
    'delete': '删除',
    'login': '登录',
    'logout': '登出',
    'transfer': '转移',
    'export': '导出',
    'import': '导入'
  }
  return map[operation] || operation
}

// 加载日志列表
async function loadLogs() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchParams.value
    }
    const res = await getOperationLogsApi(params)
    logs.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载操作日志失败:', error)
    alert('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadLogs()
}

// 分页切换
function changePage(page) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadLogs()
}

onMounted(() => {
  loadLogs()
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
  align-items: center;
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
  font-size: 13px;
}

.data-table th {
  background: #f8f9fa;
  padding: 12px 10px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e9ecef;
  white-space: nowrap;
}

.data-table td {
  padding: 10px;
  border-bottom: 1px solid #e9ecef;
  color: #555;
  vertical-align: top;
}

.data-table tr:hover {
  background: #f8f9fa;
}

/* 模块标签 */
.module-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.module-customer { background: #e3f2fd; color: #1976d2; }
.module-followup { background: #f3e5f5; color: #7b1fa2; }
.module-user { background: #e8f5e9; color: #388e3c; }
.module-system { background: #fff3e0; color: #f57c00; }

/* 操作标签 */
.action-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.action-create { background: #e8f5e9; color: #2e7d32; }
.action-update { background: #e3f2fd; color: #1565c0; }
.action-delete { background: #ffebee; color: #c62828; }
.action-login { background: #e0f2f1; color: #00695c; }
.action-logout { background: #fff3e0; color: #ef6c00; }
.action-transfer { background: #f3e5f5; color: #6a1b9a; }

/* 结果标签 */
.result-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.result-badge.success {
  background: #e8f5e9;
  color: #2e7d32;
}

.result-badge.failed {
  background: #ffebee;
  color: #c62828;
}

/* 详情文本 */
.details-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  font-size: 16px;
  margin: 0;
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
</style>
