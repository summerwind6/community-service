<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="profile-avatar">
            <el-avatar :size="80" icon="UserFilled" />
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
              {{ userInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
          <el-divider />
          <div class="profile-info">
            <p><el-icon><Phone /></el-icon> {{ userInfo.phone || '未设置' }}</p>
            <p><el-icon><Message /></el-icon> {{ userInfo.email || '未设置' }}</p>
            <p><el-icon><Clock /></el-icon> {{ userInfo.createTime }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><span>编辑资料</span></template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { updateProfile } from '@/api/auth/auth'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref({})
const saving = ref(false)

const form = reactive({
  realName: '',
  phone: '',
  email: ''
})

onMounted(async () => {
  await userStore.getUserInfo()
  userInfo.value = userStore.userInfo
  form.realName = userInfo.value.realName || ''
  form.phone = userInfo.value.phone || ''
  form.email = userInfo.value.email || ''
})

async function handleSave() {
  saving.value = true
  try {
    await updateProfile(form)
    ElMessage.success('资料更新成功')
    await userStore.getUserInfo()
  } catch {
    // error handled by interceptor
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
.profile-avatar {
  text-align: center;
  h3 { margin: 12px 0 8px; }
}
.profile-info p {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}
</style>
