<template>
  <div class="login-page">
    <div class="login-wrapper">
      <div class="login-brand">
        <el-icon :size="48" color="#409EFF"><HomeFilled /></el-icon>
        <h1>社区便民服务</h1>
        <p>申请与进度管理系统</p>
      </div>

      <el-card class="login-card" shadow="always">
        <h3>管理员登录</h3>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              :prefix-icon="UserFilled"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-extra">
          <span class="tip">默认账号 admin / admin123</span>
          <el-divider />
          <el-button text type="primary" @click="$router.push('/progress')">
            <el-icon><Search /></el-icon> 居民申请进度查询
          </el-button>
        </div>
      </el-card>

      <div class="login-footer">社区便民服务申请与进度管理系统 &copy; 2026</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch {
        // handled by interceptor
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}

.login-wrapper {
  width: 420px;
}

.login-brand {
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

.login-card {
  h3 {
    text-align: center;
    margin-bottom: 24px;
    color: #303133;
    font-size: 18px;
  }

  .el-form {
    .login-btn {
      width: 100%;
    }
  }

  .login-extra {
    text-align: center;

    .tip {
      font-size: 12px;
      color: #C0C4CC;
    }
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #909399;
}
</style>
