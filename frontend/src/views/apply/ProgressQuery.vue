<template>
  <div class="progress-page">
    <div class="progress-wrapper">
      <div class="progress-brand">
        <el-icon :size="48" color="#409EFF"><HomeFilled /></el-icon>
        <h1>社区便民服务</h1>
        <p>申请进度查询</p>
      </div>

      <el-card class="query-card" shadow="always">
        <h3>输入手机号查询申请进度</h3>

        <div class="query-form">
          <el-input
            v-model="phone"
            placeholder="请输入登记的手机号码"
            :prefix-icon="Phone"
            size="large"
            style="width: 300px"
            maxlength="11"
            clearable
            @keyup.enter="handleQuery"
          />
          <el-button type="primary" size="large" :loading="querying" @click="handleQuery">
            <el-icon><Search /></el-icon> 查询
          </el-button>
        </div>

        <div v-if="searched" class="result-section">
          <el-divider />
          <h4 v-if="applications.length > 0">共找到 {{ applications.length }} 条申请记录</h4>

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

          <el-empty v-if="applications.length === 0" description="未找到相关申请记录" />
        </div>
      </el-card>

      <div class="progress-footer">
        <el-button text @click="$router.push('/login')">管理员登录</el-button>
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
  } catch {
    applications.value = []
    searched.value = true
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    querying.value = false
  }
}
</script>

<style scoped lang="scss">
.progress-page {
  min-height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  background-color: #f0f2f5;
  padding: 60px 20px;
}

.progress-wrapper {
  width: 100%;
  max-width: 700px;
}

.progress-brand {
  text-align: center;
  margin-bottom: 32px;

  h1 {
    font-size: 26px;
    color: #303133;
    margin: 12px 0 4px;
    font-weight: 600;
  }

  p {
    font-size: 14px;
    color: #909399;
  }
}

.query-card {
  h3 {
    text-align: center;
    margin-bottom: 24px;
    color: #303133;
    font-size: 18px;
  }

  .query-form {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12px;
  }

  .result-section {
    margin-top: 8px;

    h4 {
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
          font-size: 14px;
        }
      }
    }
  }
}

.progress-footer {
  text-align: center;
  margin-top: 24px;
}
</style>
