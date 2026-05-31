<template>
    <div class="login-page">
        <div class="login-decoration"></div>
        <div class="login-box">
            <div class="login-header">
                <div class="header-icon">
                    <el-icon :size="40"><HomeFilled /></el-icon>
                </div>
                <h1>社区便民服务申请与进度管理系统</h1>
                <p>街道便民服务中心</p>
            </div>

            <el-form
                ref="loginFormRef"
                :model="loginForm"
                :rules="loginRules"
                @keyup.enter="handleLogin"
            >
                <el-form-item prop="username">
                    <el-input
                        v-model="loginForm.username"
                        placeholder="请输入账号"
                        size="large"
                        :prefix-icon="UserFilled"
                    />
                </el-form-item>

                <el-form-item prop="password">
                    <el-input
                        v-model="loginForm.password"
                        type="password"
                        placeholder="请输入密码"
                        size="large"
                        show-password
                        :prefix-icon="Lock"
                    />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleLogin">
                        登 录
                    </el-button>
                </el-form-item>

                <el-divider>
                    <span class="divider-text">或</span>
                </el-divider>

                <el-form-item>
                    <el-button size="large" class="resident-btn" @click="enterResidentApply">
                        <el-icon style="margin-right: 6px"><User /></el-icon>
                        居民办事入口
                    </el-button>
                </el-form-item>
                <div class="resident-hint">无需登录，直接申请办理</div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Lock, UserFilled, User } from '@element-plus/icons-vue'
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
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
    ]
}

onMounted(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userRoles')
})

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

function enterResidentApply() {
    router.push('/resident/apply')
}
</script>

<style scoped lang="scss">
.login-page {
    width: 100%;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e8f0fe 0%, #f0f2f5 40%, #fef0f0 100%);
    position: relative;
    overflow: hidden;
}

.login-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #304156, #409EFF, #304156);
}

.login-box {
    width: 420px;
    padding: 48px 40px 36px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08);
    position: relative;
}

.login-header {
    text-align: center;
    margin-bottom: 36px;

    .header-icon {
        width: 72px;
        height: 72px;
        margin: 0 auto 16px;
        background: linear-gradient(135deg, #304156 0%, #3b5998 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
    }

    h1 {
        font-size: 18px;
        color: #303133;
        font-weight: 600;
        margin: 0 0 6px;
        letter-spacing: 1px;
    }

    p {
        font-size: 13px;
        color: #909399;
        margin: 0;
    }
}

.submit-btn,
.resident-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
}

.submit-btn {
    letter-spacing: 8px;
    background: linear-gradient(135deg, #304156 0%, #3b5998 100%);
    border: none;

    &:hover {
        opacity: 0.9;
    }
}

.resident-btn {
    color: #67C23A;
    border-color: #b3e19d;
    background: #f0f9eb;

    &:hover {
        color: #67C23A;
        background: #e1f3d8;
        border-color: #95d475;
    }
}

.divider-text {
    color: #c0c4cc;
    font-size: 12px;
}

.resident-hint {
    text-align: center;
    font-size: 12px;
    color: #c0c4cc;
    margin-top: -8px;
}
</style>
