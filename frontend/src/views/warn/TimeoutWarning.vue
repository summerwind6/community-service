<template>
    <div class="page-container">
        <el-card shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>超时预警</span>
                    <el-tag type="danger" effect="dark">共 {{ total }} 条超时申请</el-tag>
                </div>
            </template>

            <div class="search-form">
                <el-form :inline="true" :model="searchForm">
                    <el-form-item label="关键词">
                        <el-input v-model="searchForm.keyword" placeholder="申请编号/服务事项/居民姓名" clearable />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <el-table :data="tableData" border stripe v-loading="loading">
                <el-table-column prop="applyNo" label="申请编号" min-width="160" />
                <el-table-column prop="serviceItemName" label="服务事项" min-width="180" />
                <el-table-column prop="createTime" label="提交时间" width="180" />
                <el-table-column prop="processingDays" label="承诺天数" width="100" />
                <el-table-column prop="timeoutDays" label="已超时天数" width="120">
                    <template #default="{ row }">
                        <span class="timeout-days">{{ row.timeoutDays }} 天</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="当前状态" width="120">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'COMPLETED' ? 'success' : row.status === 'REJECTED' ? 'info' : 'warning'">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link size="small" @click="handleUrge(row)">催办</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="pageNum"
                    v-model:page-size="pageSize"
                    :total="total"
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="loadData"
                    @current-change="loadData"
                />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTimeoutWarnList, urgeTimeoutApplication } from '@/api/warn/warn'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
    keyword: ''
})

async function loadData() {
    loading.value = true
    try {
        const res = await getTimeoutWarnList({
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            ...searchForm
        })
        const pageData = res.data || {}
        tableData.value = pageData.records || []
        total.value = pageData.total || 0
    } catch {
        // handled by interceptor
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
    pageNum.value = 1
    loadData()
}

function handleUrge(row) {
    ElMessageBox.confirm(`确定对【${row.applyNo}】执行催办吗？`, '确认催办', {
        type: 'warning'
    }).then(async () => {
        await urgeTimeoutApplication(row.applicationId)
        ElMessage.success('催办成功')
        loadData()
    }).catch(() => {})
}

onMounted(() => {
    loadData()
})
</script>

<style scoped lang="scss">
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.timeout-days {
    color: #f56c6c;
    font-weight: 700;
}
</style>
