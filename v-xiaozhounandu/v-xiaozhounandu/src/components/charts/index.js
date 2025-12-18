// 图表组件统一导出
export { default as BaseChart } from './BaseChart.vue'
export { default as TrendChart } from './TrendChart.vue'
export { default as IndustryChart } from './IndustryChart.vue'
export { default as LevelRadarChart } from './LevelRadarChart.vue'
export { default as MonthlyChart } from './MonthlyChart.vue'

// 图表配置
export const CHART_COLORS = [
  '#3498db', '#27ae60', '#f39c12', '#e74c3c',
  '#9b59b6', '#1abc9c', '#34495e', '#e67e22'
]

export const CHART_DEFAULTS = {
  backgroundColor: 'transparent',
  textStyle: {
    color: '#666',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif'
  },
  tooltip: {
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    borderColor: 'transparent',
    textStyle: {
      color: '#fff'
    }
  }
}