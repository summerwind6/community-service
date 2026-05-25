<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <span>数据报表导出</span>
      </template>

      <el-form :inline="true" :model="form" label-width="90px">
        <el-form-item label="报表类型">
          <el-select v-model="form.type" style="width: 220px">
            <el-option label="申请报表" value="apply" />
            <el-option label="居民报表" value="resident" />
            <el-option label="评价报表" value="evaluation" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'apply'" label="状态">
          <el-select v-model="form.status" clearable style="width: 160px">
            <el-option label="待受理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'apply'" label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item v-if="form.type === 'apply'" label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
      </el-form>

      <el-button type="primary" :loading="loading" @click="handleExport">导出 Excel</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { exportApplyReport, exportEvaluationReport, exportResidentReport } from '@/api/report/report'

const loading = ref(false)
const form = reactive({
  type: 'apply',
  status: '',
  startTime: '',
  endTime: ''
})

function downloadBlob(blob, filename) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  window.URL.revokeObjectURL(url)
}

async function handleExport() {
  loading.value = true
  try {
    let res
    if (form.type === 'resident') {
      res = await exportResidentReport()
    } else if (form.type === 'evaluation') {
      res = await exportEvaluationReport()
    } else {
      res = await exportApplyReport({
        status: form.status || undefined,
        startTime: form.startTime || undefined,
        endTime: form.endTime || undefined
      })
    }
    downloadBlob(res, `${form.type}.xlsx`)
    ElMessage.success('导出已开始')
  } finally {
    loading.value = false
  }
}
</script>
