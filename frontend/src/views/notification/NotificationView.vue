<template>
  <div class="page-container">
    <el-card shadow="hover" class="notification-card">
      <template #header>
        <div class="card-header">
          <span>消息通知</span>
          <div>
            <el-select v-model="filterType" placeholder="类型" clearable style="width:120px;margin-right:8px;" @change="loadData">
              <el-option label="全部类型" value="" />
              <el-option label="审核通知" value="AUDIT" />
              <el-option label="系统通知" value="SYSTEM" />
            </el-select>
            <el-select v-model="filterRead" placeholder="状态" clearable style="width:100px;margin-right:8px;" @change="loadData">
              <el-option label="全部" :value="null" />
              <el-option label="未读" :value="0" />
              <el-option label="已读" :value="1" />
            </el-select>
            <el-button type="primary" icon="Plus" @click="handleSend">发送通知</el-button>
          </div>
        </div>
      </template>

      <div class="notification-layout">
        <!-- 左侧通知列表 -->
        <div class="list-panel">
          <div v-loading="loading" class="list-container">
            <div
              v-for="item in tableData"
              :key="item.id"
              class="notify-item"
              :class="{ active: currentRow?.id === item.id, read: item.isRead === 1 }"
              @click="handleRowClick(item)"
            >
              <div class="item-header">
                <el-tag :type="item.type === 'AUDIT' ? 'warning' : 'info'" size="small">
                  {{ item.type === 'AUDIT' ? '审核' : '系统' }}
                </el-tag>
                <span v-if="item.isRead === 0" class="unread-dot"></span>
              </div>
              <div class="item-title" :class="{ bold: item.isRead === 0 }">{{ item.title }}</div>
              <div class="item-time">{{ item.createTime }}</div>
            </div>
            <el-empty v-if="!loading && tableData.length === 0" description="暂无通知" />
          </div>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              small
              @size-change="loadData"
              @current-change="loadData"
            />
          </div>
        </div>

        <!-- 右侧详情 -->
        <div class="detail-panel">
          <template v-if="currentRow">
            <h3>{{ currentRow.title }}</h3>
            <div class="detail-meta">
              <el-tag :type="currentRow.type === 'AUDIT' ? 'warning' : 'info'" size="small">
                {{ currentRow.type === 'AUDIT' ? '审核通知' : '系统通知' }}
              </el-tag>
              <span class="meta-time">{{ currentRow.createTime }}</span>
            </div>
            <el-divider />
            <div class="detail-content">{{ currentRow.content }}</div>
            <div class="detail-receiver">接收人：{{ currentRow.targetName }}{{ currentRow.targetPhone ? ` (${currentRow.targetPhone})` : '' }}</div>
          </template>
          <el-empty v-else description="点击左侧通知查看详情" />
        </div>
      </div>
    </el-card>

    <!-- 发送通知弹窗 -->
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
const filterRead = ref(null)
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
      type: filterType.value || undefined,
      isRead: filterRead.value !== null ? filterRead.value : undefined
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

<style scoped lang="scss">
.notification-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.notification-layout {
  display: flex;
  gap: 16px;
  height: 600px;
}

.list-panel {
  width: 360px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.list-container {
  flex: 1;
  overflow-y: auto;
}

.notify-item {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color .2s;

  &:hover { background-color: #f5f7fa; }
  &.active { background-color: #ecf5ff; }
  &.read .item-title { color: #909399; font-weight: normal; }

  .item-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 4px;
  }

  .unread-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #f56c6c;
  }

  .item-title {
    font-size: 14px;
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    &.bold { font-weight: bold; color: #303133; }
  }

  .item-time {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.detail-panel {
  flex: 1;
  padding: 0 8px;
  overflow-y: auto;

  h3 { margin-top: 0; font-size: 18px; }

  .detail-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
  }

  .meta-time { font-size: 13px; color: #909399; }

  .detail-content {
    line-height: 1.8;
    color: #303133;
    min-height: 100px;
  }

  .detail-receiver {
    margin-top: 16px;
    font-size: 13px;
    color: #909399;
  }
}
</style>
