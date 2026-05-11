<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <span>申请管理</span>
      </template>

      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="申请编号">
            <el-input v-model="searchForm.keyword" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待受理" value="PENDING" />
              <el-option label="处理中" value="PROCESSING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="applyNo" label="申请编号" width="180" />
        <el-table-column prop="residentName" label="申请人" width="120" />
        <el-table-column prop="serviceItemName" label="服务项目" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="120" />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small">详情</el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              type="warning" 
              link 
              size="small"
            >受理</el-button>
            <el-button 
              v-if="row.status === 'PROCESSING'" 
              type="success" 
              link 
              size="small"
            >审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getApplyList } from '@/api/apply/apply'
import { formatStatus } from '@/utils/common'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
  status: ''
})

function getStatusType(status) {
  return formatStatus(status).type
}

function getStatusText(status) {
  return formatStatus(status).text
}

async function loadData() {
  loading.value = true
  try {
    const res = await getApplyList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载申请列表失败：', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.status = ''
  pageNum.value = 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss"></style>
