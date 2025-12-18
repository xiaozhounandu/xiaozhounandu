<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>📊 数据看板</h2>
      <p class="subtitle">客户管理系统数据概览</p>
      <div class="header-actions">
        <button class="refresh-btn" @click="loadStats" :disabled="loading">
          🔄 刷新数据
        </button>
                        <select v-model="selectedPeriod" @change="loadStats" class="period-select">
          <option value="7">最近7天</option>
          <option value="30">最近30天</option>
          <option value="90">最近90天</option>
        </select>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid" v-if="stats && !loading">
      <div class="stat-card primary">
        <div class="stat-header">
          <span class="stat-label">总客户数</span>
          <span class="stat-icon">👥</span>
        </div>
        <div class="stat-value">{{ stats.totalCustomers || 0 }}</div>
        <div class="stat-trend" :class="getTrendClass(stats.newCustomers)">
          <span v-if="stats.newCustomers > 0">↗</span>
          <span v-else>→</span>
          新增 {{ stats.newCustomers || 0 }} 个 (最近30天)
        </div>
      </div>

      <div class="stat-card success">
        <div class="stat-header">
          <span class="stat-label">活跃客户</span>
          <span class="stat-icon">🤝</span>
        </div>
        <div class="stat-value">{{ stats.activeCustomers || 0 }}</div>
        <div class="stat-trend up">
          <span>↗</span>
          占比 {{ getPercentage(stats.activeCustomers, stats.totalCustomers) }}%
        </div>
      </div>

      <div class="stat-card warning">
        <div class="stat-header">
          <span class="stat-label">成交客户</span>
          <span class="stat-icon">🎉</span>
        </div>
        <div class="stat-value">{{ stats.dealedCustomers || 0 }}</div>
        <div class="stat-trend up">
          <span>↗</span>
          占比 {{ getPercentage(stats.dealedCustomers, stats.totalCustomers) }}%
        </div>
      </div>

      <div class="stat-card danger">
        <div class="stat-header">
          <span class="stat-label">流失客户</span>
          <span class="stat-icon">⚠️</span>
        </div>
        <div class="stat-value">{{ stats.lostCustomers || 0 }}</div>
        <div class="stat-trend" :class="stats.lostCustomers > 0 ? 'down' : 'success'">
          <span v-if="stats.lostCustomers > 0">↘</span>
          <span v-else>✓</span>
          占比 {{ getPercentage(stats.lostCustomers, stats.totalCustomers) }}%
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载数据...</p>
    </div>

    <!-- ECharts图表区域 -->
    <div class="charts-container" v-if="!loading">
      <!-- 7天趋势图 -->
      <div class="chart-card">
        <h3>📈 近7天客户增长趋势</h3>
        <TrendChart
          :data="stats?.recent7Days"
          :loading="loading"
          height="320px"
          @chart-click="onTrendChartClick"
        />
      </div>

      <!-- 行业分布饼图 -->
      <div class="chart-card">
        <h3>🏢 行业分布</h3>
        <IndustryChart
          :data="stats?.byIndustry"
          :loading="loading"
          height="320px"
          @chart-click="onIndustryChartClick"
        />
      </div>

      <!-- 客户等级雷达图 -->
      <div class="chart-card">
        <h3>⭐ 客户等级分析</h3>
        <LevelRadarChart
          :data="stats?.byLevel"
          :loading="loading"
          height="320px"
          @chart-click="onLevelChartClick"
        />
      </div>

      <!-- 月度客户增长折线图 -->
      <div class="chart-card">
        <h3>📊 月度客户增长趋势</h3>
        <MonthlyChart
          :data="stats?.monthlyTrend"
          :loading="loading"
          height="320px"
          @chart-click="onMonthlyChartClick"
        />
      </div>

      </div>

    <!-- 加载状态 -->
    <div v-else class="loading-charts">
      <div class="loading-spinner"></div>
      <p>正在加载图表数据...</p>
    </div>

    <!-- 客户等级分布 -->
    <div class="level-section" v-if="stats && !loading">
      <div class="chart-card">
        <h3>⭐ 客户等级分布</h3>
        <div class="level-cards">
          <!-- 如果有等级数据，显示真实数据 -->
          <template v-if="Object.keys(stats.byLevel || {}).length > 0">
            <div
              v-for="(count, level) in stats.byLevel"
              :key="level"
              class="level-card"
              :class="'level-' + (level || 'unknown').toLowerCase()"
            >
              <div class="level-icon">{{ getLevelIcon(level) }}</div>
              <div class="level-name">{{ getLevelName(level) }}</div>
              <div class="level-count">{{ count }}</div>
              <div class="level-percent">{{ getPercentage(count, stats.totalCustomers) }}%</div>
            </div>
          </template>
          <!-- 如果没有数据，显示空状态 -->
          <div v-else class="no-data-state">
            <p>暂无客户等级数据</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 跟进统计 -->
    <div class="followup-section" v-if="stats && !loading">
      <div class="followup-cards">
        <div class="followup-card">
          <div class="followup-header">
            <span class="followup-label">今日跟进</span>
            <span class="followup-icon">📋</span>
          </div>
          <div class="followup-value">{{ stats.todayFollowups || 0 }}</div>
          <div class="followup-desc">计划中</div>
        </div>

        <div class="followup-card">
          <div class="followup-header">
            <span class="followup-label">待跟进</span>
            <span class="followup-icon">⏰</span>
          </div>
          <div class="followup-value">{{ stats.upcomingFollowups || 0 }}</div>
          <div class="followup-desc">即将进行</div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions" v-if="!loading">
      <h3>🚀 快捷操作</h3>
      <div class="action-buttons">
        <router-link to="/customers/form" class="action-btn primary">
          <span class="action-icon">➕</span>
          <span class="action-text">新增客户</span>
        </router-link>
        <router-link to="/customers" class="action-btn secondary">
          <span class="action-icon">👥</span>
          <span class="action-text">查看客户</span>
        </router-link>
        <router-link to="/followups" class="action-btn secondary">
          <span class="action-icon">📝</span>
          <span class="action-text">跟进记录</span>
        </router-link>
        <router-link to="/user/profile" class="action-btn secondary">
          <span class="action-icon">👤</span>
          <span class="action-text">个人中心</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStats } from '../../api/stats'
import { formatDateTime } from '../../utils/format'
import TrendChart from '../../components/charts/TrendChart.vue'
import IndustryChart from '../../components/charts/IndustryChart.vue'
import LevelRadarChart from '../../components/charts/LevelRadarChart.vue'
import MonthlyChart from '../../components/charts/MonthlyChart.vue'

const stats = ref(null)
const loading = ref(false)
const selectedPeriod = ref('30')

// 状态标签映射
function getStatusLabel(status) {
  const map = {
    0: '删除',
    1: '正常',
    2: '已成交',
    3: '已流失'
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

// 等级映射
function getLevelName(level) {
  const map = {
    'A': 'A级客户',
    'B': 'B级客户',
    'C': 'C级客户',
    'D': 'D级客户'
  }
  return map[level] || '未分类'
}

function getLevelIcon(level) {
  const map = {
    'A': '⭐⭐⭐',
    'B': '⭐⭐',
    'C': '⭐',
    'D': '☆'
  }
  return map[level] || '？'
}

// 工具函数
function getPercentage(value, total) {
  if (!total || total === 0) return 0
  return Math.round((value / total) * 100)
}

function getTrendClass(value) {
  if (value > 0) return 'up'
  if (value < 0) return 'down'
  return 'neutral'
}

function formatDateLabel(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

function getBarHeight(count, data) {
  if (!data || data.length === 0) return '0%'
  const maxCount = Math.max(...data.map(d => d.count || 0))
  if (maxCount === 0) return '0%'
  return `${Math.max((count / maxCount) * 100, 5)}%`
}

// 图表事件处理函数
function onTrendChartClick(params) {
  console.log('趋势图点击:', params)
  // 可以在这里添加具体的业务逻辑，比如跳转到详情页面
}

function onIndustryChartClick(params) {
  console.log('行业分布图点击:', params)
  // 根据行业筛选客户
}

function onLevelChartClick(params) {
  console.log('客户等级图点击:', params)
  // 根据等级筛选客户
}

function onMonthlyChartClick(params) {
  console.log('月度趋势图点击:', params)
  // 显示该月份的详细信息
}

// 加载统计数据 - 始终使用真实数据库数据
async function loadStats() {
  loading.value = true
  try {
    const res = await getStats()
    if (res.code === 200) {
      // 即使data为空或null，也要显示真实数据
      stats.value = res.data || {}
      console.log('成功加载统计数据:', stats.value)
    } else {
      console.warn('API返回错误:', res.message)
      // 设置默认空数据结构
      stats.value = {
        totalCustomers: 0,
        newCustomers: 0,
        activeCustomers: 0,
        dealedCustomers: 0,
        lostCustomers: 0,
        todayFollowups: 0,
        upcomingFollowups: 0,
        recent7Days: [],
        byIndustry: {},
        byLevel: {},
        customerStatus: {},
        monthlyTrend: [],
        followUpByType: {}
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 设置默认空数据结构
    stats.value = {
      totalCustomers: 0,
      newCustomers: 0,
      activeCustomers: 0,
      dealedCustomers: 0,
      lostCustomers: 0,
      todayFollowups: 0,
      upcomingFollowups: 0,
      recent7Days: [],
      byIndustry: {},
      byLevel: {},
      customerStatus: {},
      monthlyTrend: [],
      followUpByType: {}
    }
  } finally {
    loading.value = false
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
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.refresh-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.refresh-btn:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-1px);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.generate-btn {
  background: #27ae60;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.generate-btn:hover:not(:disabled) {
  background: #229954;
  transform: translateY(-1px);
}

.generate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.period-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  font-size: 14px;
  cursor: pointer;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #2980b9);
}

.stat-card.primary::before { background: linear-gradient(90deg, #3498db, #2980b9); }
.stat-card.success::before { background: linear-gradient(90deg, #27ae60, #229954); }
.stat-card.warning::before { background: linear-gradient(90deg, #f39c12, #e67e22); }
.stat-card.danger::before { background: linear-gradient(90deg, #e74c3c, #c0392b); }

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stat-label {
  color: #666;
  font-size: 15px;
  font-weight: 500;
}

.stat-icon {
  font-size: 24px;
  opacity: 0.8;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 12px;
  line-height: 1;
}

.stat-trend {
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-trend.up { color: #27ae60; }
.stat-trend.down { color: #e74c3c; }
.stat-trend.neutral { color: #7f8c8d; }
.stat-trend.warning { color: #f39c12; }
.stat-trend.success { color: #27ae60; }

/* ECharts图表容器 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  }
}

@media (max-width: 768px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.chart-card h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 图表加载状态 */
.chart-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #666;
  font-size: 14px;
}

.chart-loading::before {
  content: '';
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

/* 图表加载状态 */
.loading-charts {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.loading-charts .loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}


/* 客户等级分布 */
.level-section {
  margin-bottom: 24px;
}

.level-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.level-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.level-card.level-a { border-color: #f1c40f; }
.level-card.level-b { border-color: #3498db; }
.level-card.level-c { border-color: #95a5a6; }
.level-card.level-d { border-color: #bdc3c7; }

.level-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.level-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.level-name {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
}

.level-count {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.level-percent {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

/* 跟进统计 */
.followup-section {
  margin-bottom: 24px;
}

.followup-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.followup-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.followup-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.followup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.followup-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.followup-icon {
  font-size: 20px;
}

.followup-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.followup-desc {
  font-size: 12px;
  color: #7f8c8d;
}

/* 快捷操作 */
.quick-actions {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.quick-actions h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 10px;
  text-decoration: none;
  transition: all 0.3s ease;
  gap: 8px;
}

.action-btn.primary {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.action-btn.secondary {
  background: #f8f9fa;
  color: #555;
  border: 1px solid #e9ecef;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, #2980b9, #21618c);
}

.action-btn.secondary:hover {
  background: #e9ecef;
  color: #2c3e50;
}

.action-icon {
  font-size: 24px;
}

.action-text {
  font-size: 14px;
  font-weight: 600;
}

/* 无数据状态 */
.no-data {
  text-align: center;
  padding: 40px;
  color: #7f8c8d;
  font-style: italic;
}

.no-data-state {
  text-align: center;
  padding: 40px;
  color: #7f8c8d;
  font-style: italic;
  grid-column: 1 / -1;
}

.no-data-state p {
  margin: 0;
  font-size: 14px;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    grid-template-columns: 1fr;
  }

  .level-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .followup-cards {
    grid-template-columns: 1fr;
  }

  .industry-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .industry-bar {
    order: 2;
  }

  .industry-count {
    order: 1;
    text-align: left;
  }
}
</style>
