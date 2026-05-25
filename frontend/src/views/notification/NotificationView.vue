<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <span>消息通知</span>
      </template>

      <div style="margin-bottom:16px;">
        <el-button type="primary" icon="Plus" @click="handleSend">发送通知</el-button>
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width:150px;margin-left:12px;" @change="loadData">
          <el-option label="全部" value="" />
          <el-option label="审核通知" value="AUDIT" />
          <el-option label="系统通知" value="SYSTEM" />
        </el-select>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" @row-click="handleRowClick" highlight-current-row>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'success' : 'danger'" size="small">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <span :style="{ fontWeight: row.isRead === 0 ? 'bold' : 'normal' }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'AUDIT' ? 'warning' : 'info'" size="small">
              {{ row.type === 'AUDIT' ? '审核通知' : '系统通知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="接收人" width="100" />
        <el-table-column prop="createTime" label="发送时间" width="180" />
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

    <el-drawer v-model="detailVisible" title="通知详情" size="400px">
      <template v-if="currentRow">
        <h3>{{ currentRow.title }}</h3>
        <el-divider />
        <div style="margin-bottom:16px;">
          <el-tag :type="currentRow.type === 'AUDIT' ? 'warning' : 'info'" size="small">
            {{ currentRow.type === 'AUDIT' ? '审核通知' : '系统通知' }}
          </el-tag>
          <span style="margin-left:12px;color:#909399;">{{ currentRow.createTime }}</span>
        </div>
        <p style="line-height:1.8;color:#303133;">{{ currentRow.content }}</p>
        <p style="color:#909399;">接收人：{{ currentRow.targetName }} ({{ currentRow.targetPhone }})</p>
      </template>
    </el-drawer>

    <el-dialog v-model="sendVisible" title="发送通知" width="500px">
      <el-form ref="sendFormRef" :model="sendForm" :rules="sendRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="通知标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="sendForm.type">
            <el-radio label="SYSTEM">系统通知</el-radio>
            <el-radio label="AUDIT">审核通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="接收人">
          <el-input v-model="sendForm.targetName" placeholder="姓名" style="width:45%;" />
          <el-input v-model="sendForm.targetPhone" placeholder="电话" style="width:45%;margin-left:8%;" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="sendForm.content" type="textarea" rows="5" placeholder="通知内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="handleSendSubmit">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getNotificationList, sendNotification, markAsRead } from '@/api/notification/notification'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filterType = ref('')
const detailVisible = ref(false)
const currentRow = ref(null)
const sendVisible = ref(false)
const sending = ref(false)
const sendFormRef = ref()

const sendForm = reactive({ title: '', type: 'SYSTEM', targetName: '', targetPhone: '', content: '' })
const sendRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getNotificationList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: filterType.value || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleRowClick(row) {
  currentRow.value = row
  detailVisible.value = true
  if (row.isRead === 0) {
    try { await markAsRead(row.id); row.isRead = 1 } catch { /* ignore */ }
  }
}

function handleSend() {
  sendForm.title = ''
  sendForm.type = 'SYSTEM'
  sendForm.targetName = ''
  sendForm.targetPhone = ''
  sendForm.content = ''
  sendVisible.value = true
}

async function handleSendSubmit() {
  if (!sendFormRef.value) return
  await sendFormRef.value.validate(async (valid) => {
    if (!valid) return
    sending.value = true
    try {
      await sendNotification({ ...sendForm })
      sendVisible.value = false
      loadData()
    } catch {
      // handled by interceptor
    } finally {
      sending.value = false
    }
  })
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss"></style>
