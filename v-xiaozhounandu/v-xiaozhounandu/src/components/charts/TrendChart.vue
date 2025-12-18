<template>
  <BaseChart
    :option="chartOption"
    :height="height"
    :loading="loading"
    :error="error"
    @chart-ready="onChartReady"
    @chart-click="onChartClick"
  />
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  height: {
    type: String,
    default: '300px'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['chartClick'])

const error = ref('')

// 计算图表选项
const chartOption = computed(() => {
  if (!props.data || props.data.length === 0) {
    return null
  }

  const dates = props.data.map(item => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })
  const counts = props.data.map(item => item.count || 0)

  return {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        return `${data.name}<br/>新增客户: ${data.value}个`
      },
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: { color: '#e0e0e0' }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      }
    },
    series: [{
      name: '新增客户',
      type: 'bar',
      data: counts,
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3498db' },
          { offset: 1, color: '#2980b9' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#5dade2' },
            { offset: 1, color: '#3498db' }
          ])
        },
        shadowBlur: 10,
        shadowColor: 'rgba(52, 152, 219, 0.3)'
      }
    }],
    animationDelay: (idx) => idx * 100
  }
})

// 图表事件处理
function onChartReady(chart) {
  // 图表加载完成后的回调
  console.log('Trend chart ready')
}

function onChartClick(params) {
  emit('chartClick', params)
}

// 监听数据变化
watch(() => props.data, (newData) => {
  if (!newData || newData.length === 0) {
    error.value = '暂无数据'
  } else {
    error.value = ''
  }
}, { immediate: true })
</script>