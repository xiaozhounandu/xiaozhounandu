<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>📊 数据看板</h2>
      <p class="subtitle">客户管理系统数据概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid" v-if="stats">
      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">总客户数</span>
          <span class="stat-icon">👥</span>
        </div>
        <div class="stat-value">{{ stats.totalCustomers }}</div>
        <div class="stat-trend" :class="stats.customerGrowth >= 0 ? 'up' : 'down'">
          <span v-if="stats.customerGrowth >= 0">↗</span>
          <span v-else>↘</span>
          {{ Math.abs(stats.customerGrowth) }}% 较昨日
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">今日跟进</span>
          <span class="stat-icon">📝</span>
        </div>
        <div class="stat-value">{{ stats.todayFollowups }}</div>
        <div class="stat-trend up">
          <span>↗</span>
          {{ stats.todayFollowups }} 今日
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">本周新增</span>
          <span class="stat-icon">📈</span>
        </div>
        <div class="stat-value">{{ stats.weekAdded }}</div>
        <div class="stat-trend up">
          <span>↗</span>
          较上周
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">待跟进</span>
          <span class="stat-icon">⏰</span>
        </div>
        <div class="stat-value">{{ stats.pendingFollowups }}</div>
        <div class="stat-trend" :class="stats.pendingFollowups > 0 ? 'warning' : 'success'">
          <span v-if="stats.pendingFollowups > 0">⚠️</span>
          <span v-else>✓</span>
          {{ stats.pendingFollowups > 0 ? '需要关注' : '全部完成' }}
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-state">
      <div class="spinner"></div>
      <p>正在加载数据...</p>
    </div>

    <!-- 客户状态分布图表 -->
    <div class="chart-section">
      <h3>客户状态分布</h3>
      <div class="status-bars" v-if="stats">
        <div
          v-for="(count, status) in stats.statusDistribution"
          :key="status"
          class="status-bar"
          :style="{ width: (count / stats.totalCustomers * 100) + '%' }"
          :class="'status-' + status"
        >
          <span class="status-name">{{ getStatusLabel(status) }}</span>
          <span class="status-count">{{ count }}</span>
        </div>
      </div>
    </div>

    <!-- 最近跟进记录 -->
    <div class="recent-followups" v-if="stats && stats.recentFollowups.length > 0">
      <h3>最近跟进记录</h3>
      <table class="data-table">
        <thead>
          <tr>
            <th>客户名称</th>
            <th>跟进方式</th>
            <th>内容摘要</th>
            <th>跟进人</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in stats.recentFollowups" :key="item.id">
            <td>{{ item.customerName }}</td>
            <td>
              <span class="badge" :class="'type-' + item.type">{{ getFollowupTypeLabel(item.type) }}</span>
            </td>
            <td class="truncate">{{ item.content }}</td>
            <td>{{ item.userName }}</td>
            <td>{{ formatDateTime(item.followTime) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStats } from '../../api/stats'
import { formatDateTime } from '../../utils/format'

const stats = ref(null)

// 状态标签映射
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

// 跟进类型映射
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

// 加载统计数据
async function loadStats() {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
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

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
  border-left: 4px solid #3498db;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.stat-icon {
  font-size: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.stat-trend.up {
  color: #27ae60;
}

.stat-trend.down {
  color: #e74c3c;
}

.stat-trend.warning {
  color: #e67e22;
}

.stat-trend.success {
  color: #27ae60;
}

/* 图表区域 */
.chart-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.chart-section h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 18px;
}

.status-bars {
  display: flex;
  height: 48px;
  border-radius: 6px;
  overflow: hidden;
  background: #ecf0f1;
}

.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  color: white;
  font-size: 12px;
  font-weight: 600;
  transition: opacity 0.2s;
  cursor: pointer;
}

.status-bar:hover {
  opacity: 0.9;
}

.status-potential { background: #3498db; }
.status-contacted { background: #9b59b6; }
.status-negotiating { background: #e67e22; }
.status-success { background: #27ae60; }
.status-failed { background: #e74c3c; }

/* 最近跟进记录 */
.recent-followups {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.recent-followups h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 18px;
}

/* 表格样式 */
.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8f9fa;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e9ecef;
}

.data-table td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  color: #555;
}

.data-table tr:hover {
  background: #f8f9fa;
}

.truncate {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  color: white;
}

.type-phone { background: #3498db; }
.type-visit { background: #27ae60; }
.type-email { background: #9b59b6; }
.type-wechat { background: #2ecc71; }
.type-meeting { background: #e67e22; }

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
