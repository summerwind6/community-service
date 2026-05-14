<template>
  <div class="progress-page">
    <div class="query-card">
      <h2>申请进度查询</h2>
      <p class="subtitle">输入登记的手机号，查看您的便民服务申请进度</p>

      <el-form :inline="true" class="query-form">
        <el-form-item>
          <el-input
            v-model="phone"
            placeholder="请输入手机号码"
            :prefix-icon="Phone"
            size="large"
            style="width: 280px"
            maxlength="11"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="querying" @click="handleQuery">
            查询
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="searched && applications.length === 0" class="empty-result">
        <el-empty description="未找到相关申请记录">
          <template #image>
            <el-icon :size="60" color="#C0C4CC"><Document /></el-icon>
          </template>
        </el-empty>
      </div>

      <div v-if="applications.length > 0" class="result-list">
        <h3>共找到 {{ applications.length }} 条申请记录</h3>
        <el-card
          v-for="app in applications" :key="app.id"
          shadow="hover"
          class="apply-item"
        >
          <div class="apply-header">
            <span class="apply-no">{{ app.applyNo }}</span>
            <el-tag :type="statusMap[app.status]?.type || 'info'">
              {{ statusMap[app.status]?.text || app.status }}
            </el-tag>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="服务项目">{{ app.serviceItemName }}</el-descriptions-item>
            <el-descriptions-item label="费用">¥{{ app.fee }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ app.createTime }}</el-descriptions-item>
            <el-descriptions-item label="处理人">{{ app.handlerName || '-' }}</el-descriptions-item>
            <el-descriptions-item v-if="app.remark" label="备注" :span="2">{{ app.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Phone } from '@element-plus/icons-vue'
import { queryProgress } from '@/api/apply/progress'
import { ElMessage } from 'element-plus'

const phone = ref('')
const querying = ref(false)
const searched = ref(false)
const applications = ref([])

const statusMap = {
  PENDING: { text: '待受理', type: 'warning' },
  PROCESSING: { text: '处理中', type: '' },
  COMPLETED: { text: '已完成', type: 'success' },
  REJECTED: { text: '已驳回', type: 'danger' }
}

async function handleQuery() {
  if (!phone.value || !/^1[3-9]\d{9}$/.test(phone.value)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }
  querying.value = true
  try {
    const res = await queryProgress(phone.value)
    applications.value = res.data || []
    searched.value = true
    if (applications.value.length === 0) {
      ElMessage.info('未找到相关申请记录')
    }
  } catch (error) {
    applications.value = []
    searched.value = true
    // 401 from Shiro for this endpoint shouldn't happen since it's anon
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    querying.value = false
  }
}
</script>

<style scoped lang="scss">
.progress-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 60px 20px;
}

.query-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 100%;
  max-width: 700px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);

  h2 {
    text-align: center;
    margin-bottom: 8px;
    color: #303133;
  }

  .subtitle {
    text-align: center;
    color: #909399;
    margin-bottom: 30px;
  }

  .query-form {
    display: flex;
    justify-content: center;
  }

  .empty-result {
    margin-top: 20px;
  }

  .result-list {
    margin-top: 20px;

    h3 {
      margin-bottom: 16px;
      color: #606266;
      font-size: 15px;
    }

    .apply-item {
      margin-bottom: 16px;

      .apply-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .apply-no {
          font-weight: bold;
          color: #409EFF;
        }
      }
    }
  }
}
</style>
