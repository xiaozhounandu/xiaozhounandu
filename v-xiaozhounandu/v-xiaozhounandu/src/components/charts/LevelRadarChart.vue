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
    type: Object,
    default: () => ({})
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

// 等级映射
const levelMap = {
  'A': 'A级客户',
  'B': 'B级客户',
  'C': 'C级客户',
  'D': 'D级客户'
}

// 计算图表选项
const chartOption = computed(() => {
  if (!props.data || Object.keys(props.data).length === 0) {
    return null
  }

  const data = []
  const indicators = []

  // 构建数据
  Object.entries(props.data).forEach(([key, value]) => {
    const levelName = levelMap[key] || key
    indicators.push({
      name: levelName,
      max: 500, // 动态计算最大值
      axisLabel: { color: '#666' }
    })
    data.push(value)
  })

  const maxValue = Math.max(...data, 100) * 1.2

  // 更新指示器的最大值
  indicators.forEach(indicator => {
    indicator.max = maxValue
  })

  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      }
    },
    radar: {
      indicator: indicators,
      shape: 'polygon',
      splitNumber: 4,
      axisName: {
        color: '#666',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      splitArea: {
        areaStyle: {
          color: ['#f8f9fa', '#fff']
        }
      },
      axisLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      }
    },
    series: [{
      name: '客户等级分布',
      type: 'radar',
      data: [{
        value: data,
        name: '客户分布',
        itemStyle: {
          color: '#3498db'
        },
        lineStyle: {
          width: 2,
          color: '#3498db'
        },
        areaStyle: {
          color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
            { offset: 0, color: 'rgba(52, 152, 219, 0.3)' },
            { offset: 1, color: 'rgba(52, 152, 219, 0.1)' }
          ])
        },
        symbol: 'circle',
        symbolSize: 6,
        emphasis: {
          itemStyle: {
            color: '#2980b9',
            borderColor: '#2980b9',
            borderWidth: 2
          },
          areaStyle: {
            color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
              { offset: 0, color: 'rgba(41, 128, 185, 0.4)' },
              { offset: 1, color: 'rgba(41, 128, 185, 0.2)' }
            ])
          }
        }
      }]
    }],
    animationDuration: 1500,
    animationEasing: 'elasticOut'
  }
})

// 图表事件处理
function onChartReady(chart) {
  console.log('Level radar chart ready')
}

function onChartClick(params) {
  emit('chartClick', params)
}

// 监听数据变化
watch(() => props.data, (newData) => {
  if (!newData || Object.keys(newData).length === 0) {
    error.value = '暂无等级数据'
  } else {
    error.value = ''
  }
}, { immediate: true })
</script>