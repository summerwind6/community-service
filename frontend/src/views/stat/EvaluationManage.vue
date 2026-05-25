<template>
  <div class="page-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="label">评价总数</div>
            <div class="value">{{ stats.total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="label">平均分</div>
            <div class="value">{{ stats.avgScore }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="label">好评率</div>
            <div class="value">{{ stats.goodRate }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <span>评分分布</span>
          </template>
          <div ref="chartRef" style="height: 360px;"></div>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>评价列表</span>
              <el-select v-model="searchForm.score" placeholder="全部评分" clearable style="width: 140px" @change="handleSearch">
                <el-option v-for="n in 5" :key="n" :label="`${n} 星`" :value="n" />
              </el-select>
            </div>
          </template>

          <el-table :data="tableData" border stripe v-loading="loading">
            <el-table-column prop="applyNo" label="申请编号" width="170" />
            <el-table-column prop="serviceItemName" label="服务事项" min-width="140" />
            <el-table-column prop="score" label="评分" width="120">
              <template #default="{ row }">
                <el-rate v-model="row.score" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="content" label="评价内容" min-width="180" show-overflow-tooltip />
            <el-table-column prop="createTime" label="评价时间" width="180" />
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadData"
              @current-change="loadData"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import { getEvaluationList, getEvaluationStats } from '@/api/evaluation/evaluation'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchForm = reactive({
  score: ''
})
const stats = reactive({
  total: 0,
  avgScore: '0.00',
  goodRate: '0.00'
})
const chartRef = ref()
let chart = null

async function loadData() {
  loading.value = true
  try {
    const res = await getEvaluationList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      score: searchForm.score || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  const res = await getEvaluationStats()
  const data = res.data || {}
  stats.total = data.total || 0
  stats.avgScore = Number(data.avgScore || 0).toFixed(2)
  stats.goodRate = Number(data.goodRate || 0).toFixed(2)

  const distribution = data.distribution || []
  chart?.setOption({
    xAxis: { data: ['1星', '2星', '3星', '4星', '5星'] },
    series: [{ data: distribution }]
  })
}

function initChart() {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', data: [], itemStyle: { color: '#409EFF' } }]
  })
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleResize() {
  chart?.resize()
}

onMounted(async () => {
  initChart()
  await Promise.all([loadData(), loadStats()])
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped lang="scss">
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  .label {
    color: #909399;
    font-size: 14px;
    margin-bottom: 8px;
  }

  .value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
