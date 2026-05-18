<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>申请趋势分析</span>
              <el-radio-group v-model="period" size="small" @change="loadData">
                <el-radio-button value="week">按周</el-radio-button>
                <el-radio-button value="month">按月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>服务类型分布</span>
          </template>
          <div ref="pieChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getTrendData, getDistributionData } from '@/api/stat/stat'

const trendChartRef = ref()
const pieChartRef = ref()
const period = ref('week')
let trendChart = null
let pieChart = null

function initCharts() {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['申请量', '办结量'] },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        name: '申请量',
        type: 'line',
        smooth: true,
        data: [],
        areaStyle: { opacity: 0.3 }
      }, {
        name: '办结量',
        type: 'line',
        smooth: true,
        data: [],
        lineStyle: { color: '#67C23A' },
        itemStyle: { color: '#67C23A' },
        areaStyle: { color: '#67C23A', opacity: 0.15 }
      }]
    })
  }

  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['35%', '65%'],
        data: []
      }]
    })
  }
}

async function loadData() {
  try {
    const [trendRes, distRes] = await Promise.all([
      getTrendData({ period: period.value }),
      getDistributionData()
    ])
    if (trendChart && trendRes.data) {
      trendChart.setOption({
        xAxis: { data: trendRes.data.dates || [] },
        series: [
          { data: trendRes.data.applyCounts || [] },
          { data: trendRes.data.completedCounts || [] }
        ]
      })
    }
    if (pieChart && distRes.data) {
      const pieData = (distRes.data.categories || []).map((name, i) => ({
        name,
        value: (distRes.data.values || [])[i] || 0
      }))
      pieChart.setOption({ series: [{ data: pieData }] })
    }
  } catch {
    // handled by interceptor
  }
}

function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initCharts()
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped lang="scss">
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
