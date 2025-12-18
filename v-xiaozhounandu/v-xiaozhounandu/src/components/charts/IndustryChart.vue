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

// 计算图表选项
const chartOption = computed(() => {
  if (!props.data || Object.keys(props.data).length === 0) {
    return null
  }

  const chartData = Object.entries(props.data).map(([name, value]) => ({
    name: name || '未分类',
    value: value
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a}<br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      itemGap: 12,
      textStyle: {
        color: '#666',
        fontSize: 12
      },
      icon: 'circle'
    },
    series: [{
      name: '行业分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['65%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold',
          color: '#2c3e50',
          formatter: '{b}\n{c} ({d}%)'
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      labelLine: {
        show: false
      },
      data: chartData.sort((a, b) => b.value - a.value),
      color: ['#3498db', '#27ae60', '#f39c12', '#e74c3c', '#9b59b6', '#1abc9c', '#34495e', '#e67e22'],
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: (idx) => Math.random() * 200
    }]
  }
})

// 图表事件处理
function onChartReady(chart) {
  console.log('Industry chart ready')
}

function onChartClick(params) {
  emit('chartClick', params)
}

// 监听数据变化
watch(() => props.data, (newData) => {
  if (!newData || Object.keys(newData).length === 0) {
    error.value = '暂无行业数据'
  } else {
    error.value = ''
  }
}, { immediate: true })
</script>