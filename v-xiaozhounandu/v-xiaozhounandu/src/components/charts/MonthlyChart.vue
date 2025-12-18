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

// 生成模拟月度数据（如果没有提供数据）
const monthlyData = computed(() => {
  if (props.data && props.data.length > 0) {
    return props.data
  }

  // 模拟数据
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  let total = 300
  return months.map((month, index) => {
    const newCustomers = Math.floor(Math.random() * 50) + 60
    total += newCustomers
    return {
      month,
      newCustomers,
      totalCustomers: total
    }
  })
})

// 计算图表选项
const chartOption = computed(() => {
  const data = monthlyData.value
  if (!data || data.length === 0) {
    return null
  }

  const months = data.map(item => item.month)
  const newCustomers = data.map(item => item.newCustomers || 0)
  const totalCustomers = data.map(item => item.totalCustomers || 0)

  const maxValue = Math.max(...totalCustomers)

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      },
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      }
    },
    legend: {
      data: ['新增客户', '累计客户'],
      textStyle: {
        color: '#666',
        fontSize: 12
      },
      top: '5%',
      right: '5%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      axisPointer: {
        type: 'shadow'
      },
      axisLine: {
        lineStyle: { color: '#e0e0e0' }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '新增客户',
        position: 'left',
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
      {
        type: 'value',
        name: '累计客户',
        position: 'right',
        max: maxValue * 1.1,
        axisLine: {
          show: false
        },
        axisLabel: {
          color: '#666',
          fontSize: 12
        }
      }
    ],
    series: [
      {
        name: '新增客户',
        type: 'bar',
        data: newCustomers,
        barWidth: '40%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#27ae60' },
            { offset: 1, color: '#229954' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2ecc71' },
              { offset: 1, color: '#27ae60' }
            ])
          },
          shadowBlur: 10,
          shadowColor: 'rgba(39, 174, 96, 0.3)'
        }
      },
      {
        name: '累计客户',
        type: 'line',
        yAxisIndex: 1,
        data: totalCustomers,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#e74c3c'
        },
        lineStyle: {
          width: 2,
          color: '#e74c3c'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(231, 76, 60, 0.3)' },
            { offset: 1, color: 'rgba(231, 76, 60, 0.1)' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: '#c0392b',
            borderColor: '#c0392b',
            borderWidth: 2,
            shadowBlur: 10,
            shadowColor: 'rgba(231, 76, 60, 0.5)'
          }
        }
      }
    ],
    animationDelay: (idx) => idx * 50
  }
})

// 图表事件处理
function onChartReady(chart) {
  console.log('Monthly chart ready')
}

function onChartClick(params) {
  emit('chartClick', params)
}

// 监听数据变化
watch(() => props.data, (newData) => {
  if (!newData || newData.length === 0) {
    error.value = '暂无月度数据，使用模拟数据展示'
  } else {
    error.value = ''
  }
}, { immediate: true })
</script>