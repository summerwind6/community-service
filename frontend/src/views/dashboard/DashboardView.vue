<template>
  <div class="page-container">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="(item, index) in statCards" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">{{ item.title }}</div>
              <div class="card-value">{{ item.value }}</div>
            </div>
            <el-icon :class="['card-icon', item.iconClass]">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>申请趋势（近7天）</span>
          </template>
          <div ref="trendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>服务类型分布</span>
          </template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardData, getTrendData, getDistributionData } from '@/api/stat/stat'

const trendChartRef = ref()
const pieChartRef = ref()
let trendChart = null
let pieChart = null

const statCards = ref([
  { title: '系统用户', value: 0, icon: 'User', iconClass: 'icon-blue' },
  { title: '总申请数', value: 0, icon: 'Document', iconClass: 'icon-green' },
  { title: '待审核', value: 0, icon: 'Clock', iconClass: 'icon-orange' },
  { title: '今日完成', value: 0, icon: 'CircleCheck', iconClass: 'icon-red' }
])

async function loadDashboardData() {
  try {
    const res = await getDashboardData()
    if (res.data) {
      statCards.value[0].value = res.data.totalUsers || 0
      statCards.value[1].value = res.data.totalApplies || 0
      statCards.value[2].value = res.data.pendingAudit || 0
      statCards.value[3].value = res.data.completedToday || 0
    }
  } catch {
    // handled by interceptor
  }
}

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
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        label: { show: true, formatter: '{b}\n{d}%' },
        data: []
      }]
    })
  }
}

async function loadChartsData() {
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
  loadDashboardData()
  loadChartsData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped lang="scss">
.stat-cards {
  .stat-card {
    .card-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-info {
        .card-title {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }

        .card-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }
      }

      .card-icon {
        font-size: 48px;

        &.icon-blue { color: #409EFF; }
        &.icon-green { color: #67C23A; }
        &.icon-orange { color: #E6A23C; }
        &.icon-red { color: #F56C6C; }
      }
    }
  }
}
</style>
