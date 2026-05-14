<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>申请趋势分析</span>
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
let trendChart = null
let pieChart = null

function initCharts() {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        name: '申请数量',
        type: 'line',
        smooth: true,
        data: [],
        areaStyle: { opacity: 0.3 }
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
      getTrendData(),
      getDistributionData()
    ])
    if (trendChart && trendRes.data) {
      trendChart.setOption({
        xAxis: { data: trendRes.data.dates || [] },
        series: [{ data: trendRes.data.counts || [] }]
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

<style scoped lang="scss"></style>
