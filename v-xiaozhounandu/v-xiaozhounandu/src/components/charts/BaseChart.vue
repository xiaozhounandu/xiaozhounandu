<template>
  <div class="chart-wrapper">
    <div
      ref="chartDom"
      :class="['chart-container', loading ? 'loading' : '']"
      :style="{ height: height }"
    ></div>
    <div v-if="loading" class="chart-loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>
    <div v-if="error" class="chart-error">
      <span class="error-icon">⚠️</span>
      <span class="error-message">{{ error }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: {
    type: Object,
    required: true
  },
  height: {
    type: String,
    default: '300px'
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['chartReady', 'chartClick'])

const chartDom = ref(null)
let chartInstance = null

// 初始化图表
function initChart() {
  if (!chartDom.value) return

  // 如果已存在实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartDom.value)

  // 设置图表选项
  if (props.option) {
    chartInstance.setOption(props.option)
  }

  // 添加点击事件
  chartInstance.on('click', (params) => {
    emit('chartClick', params)
  })

  // 通知父组件图表已准备好
  emit('chartReady', chartInstance)
}

// 更新图表选项
function updateOption() {
  if (chartInstance && props.option) {
    chartInstance.setOption(props.option, true)
  }
}

// 调整图表大小
function resizeChart() {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 监听选项变化
watch(() => props.option, () => {
  nextTick(() => {
    updateOption()
  })
}, { deep: true })

// 监听loading状态
watch(() => props.loading, (newVal) => {
  if (newVal && chartInstance) {
    chartInstance.showLoading()
  } else if (!newVal && chartInstance) {
    chartInstance.hideLoading()
  }
})

onMounted(() => {
  nextTick(() => {
    initChart()

    // 监听窗口大小变化
    window.addEventListener('resize', resizeChart)
  })
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('resize', resizeChart)

  // 销毁图表实例
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

// 暴露方法给父组件
defineExpose({
  resizeChart,
  getChartInstance: () => chartInstance
})
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  width: 100%;
}

.chart-container {
  width: 100%;
  min-height: 200px;
}

.chart-container.loading {
  opacity: 0.6;
  pointer-events: none;
}

.chart-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  color: #666;
  font-size: 14px;
  gap: 8px;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.chart-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #e74c3c;
  font-size: 14px;
  gap: 8px;
}

.error-icon {
  font-size: 32px;
}

.error-message {
  color: #e74c3c;
  text-align: center;
  padding: 0 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>