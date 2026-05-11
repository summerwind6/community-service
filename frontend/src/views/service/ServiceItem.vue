<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <span>服务项目管理</span>
      </template>

      <div style="margin-bottom: 16px;">
        <el-button type="primary" icon="Plus">新增项目</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="itemName" label="项目名称" min-width="150" />
        <el-table-column prop="itemCode" label="项目编码" width="120" />
        <el-table-column prop="category" label="服务类别" width="120" />
        <el-table-column prop="fee" label="费用(元)" width="100">
          <template #default="{ row }">
            {{ row.fee === 0 ? '免费' : `¥${row.fee}` }}
          </template>
        </el-table-column>
        <el-table-column prop="processingDays" label="处理天数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="danger" link size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getItemList } from '@/api/item/item'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

async function loadData() {
  loading.value = true
  try {
    const res = await getItemList()
    tableData.value = res.data || []
    total.value = res.data?.length || 0
  } catch (error) {
    console.error('加载服务项目失败：', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss"></style>
