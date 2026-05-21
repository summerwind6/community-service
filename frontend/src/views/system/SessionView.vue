<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>在线用户</span>
          <el-button type="primary" @click="loadData">刷新</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading" empty-text="暂无在线用户">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="host" label="来源IP" width="160" />
        <el-table-column prop="startTime" label="登录时间" width="180" />
        <el-table-column prop="lastAccess" label="最后访问" width="180" />
        <el-table-column prop="timeout" label="超时时间" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-popconfirm
              title="确定强制下线该用户？"
              @confirm="handleKick(row.sessionId)"
            >
              <template #reference>
                <el-button type="danger" link size="small">强制下线</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSessionList, kickSession } from '@/api/system/session'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getSessionList()
    tableData.value = res.data || []
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleKick(sessionId) {
  try {
    await kickSession(sessionId)
    ElMessage.success('已强制下线')
    loadData()
  } catch {
    // handled by interceptor
  }
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
