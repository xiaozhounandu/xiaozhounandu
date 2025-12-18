<template>
  <div class="login-log">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>🔐 登录日志</h2>
      <p class="subtitle">系统用户登录记录</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="search-group">
        <input
          type="text"
          v-model="searchParams.keyword"
          placeholder="搜索用户名或IP地址"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="btn-search" @click="handleSearch">搜索</button>
      </div>

      <div class="filter-group">
        <select v-model="searchParams.status" @change="handleSearch" class="filter-select">
          <option value="">全部状态</option>
          <option value="success">成功</option>
          <option value="failed">失败</option>
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

    <!-- 统计卡片 -->
    <div class="stats-grid" v-if="stats">
      <div class="stat-card">
        <div class="stat-label">今日登录次数</div>
        <div class="stat-value today">{{ stats.todayLogins }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本周登录次数</div>
        <div class="stat-value">{{ stats.weekLogins }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">登录失败次数</div>
        <div class="stat-value failed">{{ stats.failedLogins }}</div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container" v-loading="loading">
      <table class="data-table" v-if="logs.length > 0">
        <thead>
          <tr>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>IP地址</th>
            <th>地理位置</th>
            <th>浏览器</th>
            <th>登录时间</th>
            <th>状态</th>
            <th>失败原因</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in logs" :key="item.id">
            <td>{{ item.username }}</td>
            <td>{{ item.realName || '-' }}</td>
            <td>{{ item.ipAddress }}</td>
            <td>{{ item.location || '未知' }}</td>
            <td>{{ item.browser || '-' }}</td>
            <td>{{ formatDateTime(item.loginTime) }}</td>
            <td>
              <span class="status-badge" :class="item.success ? 'success' : 'failed'">
                {{ item.success ? '成功' : '失败' }}
              </span>
            </td>
            <td class="reason-text">{{ item.failureReason || '-' }}</td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无登录日志</p>
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
import { getLoginLogsApi } from '../../api/log'
import { formatDateTime } from '../../utils/format'

// 数据状态
const logs = ref([])
const stats = ref(null)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(15)
const loading = ref(false)

// 搜索参数
const searchParams = ref({
  keyword: '',
  status: '',
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

// 加载日志列表
async function loadLogs() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchParams.value
    }
    const res = await getLoginLogsApi(params)
    logs.value = res.data.list
    total.value = res.data.total
    stats.value = res.data.stats
  } catch (error) {
    console.error('加载登录日志失败:', error)
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

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #667eea;
}

.stat-label {
  color: #666;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
}

.stat-value.today {
  color: #27ae60;
}

.stat-value.failed {
  color: #e74c3c;
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

/* 状态标签 */
.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.status-badge.success {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-badge.failed {
  background: #ffebee;
  color: #c62828;
}

/* 失败原因文本 */
.reason-text {
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #e74c3c;
  font-size: 12px;
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

/* 响应式 */
@media (max-width: 768px) {
  .data-table {
    font-size: 12px;
  }

  .data-table th,
  .data-table td {
    padding: 8px 6px;
  }

  .filter-group {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-date {
    max-width: none;
  }
}
</style>
