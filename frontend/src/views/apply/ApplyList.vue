<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>申请管理</span>
        </div>
      </template>

      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="编号/申请人/事项" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:120px">
              <el-option label="待受理" value="PENDING" />
              <el-option label="处理中" value="PROCESSING" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已驳回" value="REJECTED" />
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
            <el-tag :type="formatStatus(row.status).type">
              {{ formatStatus(row.status).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="120" />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="warning" link size="small"
              @click="openAudit(row, 'review')"
            >受理</el-button>
            <el-button
              v-if="row.status === 'PROCESSING'"
              type="success" link size="small"
              @click="openAudit(row, 'approve')"
            >审核</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="申请编号">{{ detailData.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="formatStatus(detailData.status).type">{{ formatStatus(detailData.status).text }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailData.residentName }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ detailData.serviceItemName }}</el-descriptions-item>
        <el-descriptions-item label="费用">¥{{ detailData.fee }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请内容" :span="2">{{ detailData.applyContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>审核记录</el-divider>
      <el-timeline v-if="auditRecords.length">
        <el-timeline-item
          v-for="r in auditRecords" :key="r.id"
          :timestamp="r.auditTime"
          :color="r.auditResult === 'APPROVED' ? '#67C23A' : '#F56C6C'"
        >
          <p><strong>{{ r.auditorName }}</strong>（{{ r.auditType === 'COMMUNITY' ? '社区初审' : '街道复审' }}）</p>
          <p>结果：{{ r.auditResult === 'APPROVED' ? '通过' : '驳回' }}</p>
          <p v-if="r.auditOpinion">意见：{{ r.auditOpinion }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无审核记录" />
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditVisible"
      :title="auditMode === 'review' ? '初审受理' : '复审审核'"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form :model="auditForm" label-width="70px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio value="APPROVED">{{ auditMode === 'review' ? '通过，进入复审' : '通过，完成办结' }}</el-radio>
            <el-radio value="REJECTED">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.opinion"
            type="textarea" :rows="3"
            placeholder="请输入审核意见（驳回时必填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAuditSubmit">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getApplyList, getApplyDetail, getAuditRecords, reviewApply, approveApply } from '@/api/apply/apply'
import { formatStatus } from '@/utils/common'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
  status: ''
})

// 详情
const detailVisible = ref(false)
const detailData = ref(null)
const auditRecords = ref([])

// 审核
const auditVisible = ref(false)
const auditMode = ref('review')
const auditTargetId = ref(null)
const submitLoading = ref(false)
const auditForm = reactive({ result: 'APPROVED', opinion: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await getApplyList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  try {
    const [detailRes, recordsRes] = await Promise.all([
      getApplyDetail(row.id),
      getAuditRecords(row.id)
    ])
    detailData.value = detailRes.data
    auditRecords.value = recordsRes.data || []
    detailVisible.value = true
  } catch {
    // handled by interceptor
  }
}

function openAudit(row, mode) {
  auditTargetId.value = row.id
  auditMode.value = mode
  auditForm.result = 'APPROVED'
  auditForm.opinion = ''
  auditVisible.value = true
}

async function handleAuditSubmit() {
  if (auditForm.result === 'REJECTED' && !auditForm.opinion) {
    ElMessage.warning('驳回时请填写审核意见')
    return
  }
  submitLoading.value = true
  try {
    if (auditMode.value === 'review') {
      await reviewApply(auditTargetId.value, { ...auditForm })
      ElMessage.success('初审完成')
    } else {
      await approveApply(auditTargetId.value, { ...auditForm })
      ElMessage.success('复审完成')
    }
    auditVisible.value = false
    loadData()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
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

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
