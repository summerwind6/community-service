<template>
    <el-container class="layout-container">
        <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
            <div class="logo">
                <el-icon :size="28" color="#fff"><HomeFilled /></el-icon>
                <span v-show="!isCollapse">社区便民服务</span>
            </div>

            <el-menu
                :default-active="$route.path"
                :collapse="isCollapse"
                background-color="#304156"
                text-color="#bfcbd9"
                active-text-color="#409EFF"
                router
            >
                <el-menu-item index="/dashboard">
                    <el-icon><Odometer /></el-icon>
                    <template #title>仪表盘</template>
                </el-menu-item>

                <el-sub-menu v-if="userStore.hasRole('ADMIN')" index="/system">
                    <template #title>
                        <el-icon><Setting /></el-icon>
                        <span>系统管理</span>
                    </template>
                    <el-menu-item index="/system/user">用户管理</el-menu-item>
                    <el-menu-item index="/system/role">角色管理</el-menu-item>
                    <el-menu-item index="/system/config">系统配置</el-menu-item>
                    <el-menu-item index="/system/session">在线用户</el-menu-item>
                </el-sub-menu>

                <el-sub-menu index="/service">
                    <template #title>
                        <el-icon><Document /></el-icon>
                        <span>服务管理</span>
                    </template>
                    <el-menu-item index="/resident/list">居民管理</el-menu-item>
                    <el-menu-item index="/service/item">服务项目</el-menu-item>
                    <el-menu-item v-if="userStore.hasRole('ADMIN')" index="/guide/manage">办事指南管理</el-menu-item>
                    <el-menu-item index="/guide/public">办事指南公开页</el-menu-item>
                    <el-menu-item index="/attachment/manage">附件管理</el-menu-item>
                </el-sub-menu>

                <el-sub-menu index="/apply">
                    <template #title>
                        <el-icon><Tickets /></el-icon>
                        <span>申请管理</span>
                    </template>
                    <el-menu-item index="/apply/list">申请列表</el-menu-item>
                    <el-menu-item index="/warn/timeout">超时预警</el-menu-item>
                    <el-menu-item
                        v-if="userStore.hasRole('ADMIN') || userStore.hasRole('CLERK')"
                        index="/appointment/list"
                    >
                        预约管理
                    </el-menu-item>
                    <el-menu-item index="/notification/list">
                        <div class="notify-item">
                            <span>消息通知</span>
                            <el-badge :value="unreadCount" :hidden="unreadCount === 0" />
                        </div>
                    </el-menu-item>
                </el-sub-menu>

                <el-sub-menu index="/stat">
                    <template #title>
                        <el-icon><DataAnalysis /></el-icon>
                        <span>统计分析</span>
                    </template>
                    <el-menu-item index="/stat/analysis">数据分析</el-menu-item>
                </el-sub-menu>

                <el-sub-menu index="/evaluation">
                    <template #title>
                        <el-icon><Star /></el-icon>
                        <span>服务评价</span>
                    </template>
                    <el-menu-item index="/evaluation">评价列表</el-menu-item>
                    <el-menu-item index="/report">数据报表</el-menu-item>
                </el-sub-menu>
            </el-menu>
        </el-aside>

        <el-container>
            <el-header class="header">
                <div class="header-left">
                    <el-icon class="collapse-btn" @click="toggleCollapse">
                        <Fold v-if="!isCollapse" />
                        <Expand v-else />
                    </el-icon>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
                    </el-breadcrumb>
                </div>
                <div class="header-right">
                    <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
                    <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" icon="UserFilled" />
            </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                                <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>

            <el-main class="main">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getUnreadCount } from '@/api/notification/notification'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const unreadCount = ref(0)
let unreadTimer = null

function toggleCollapse() {
    isCollapse.value = !isCollapse.value
}

async function fetchUnreadCount() {
    try {
        const res = await getUnreadCount()
        unreadCount.value = res.data?.count || 0
    } catch {
        unreadCount.value = 0
    }
}

onMounted(() => {
    fetchUnreadCount()
    unreadTimer = window.setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => {
    if (unreadTimer) {
        window.clearInterval(unreadTimer)
        unreadTimer = null
    }
})

async function handleCommand(command) {
    if (command === 'profile') {
        router.push('/profile')
    } else if (command === 'changePassword') {
        router.push('/profile/change-password')
    } else if (command === 'logout') {
        await userStore.logout()
        router.push('/login')
    }
}
</script>

<style scoped lang="scss">
.layout-container {
    height: 100vh;
}

.aside {
    background-color: #304156;
    transition: width 0.3s;
    overflow: hidden;

    .logo {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 18px;
        font-weight: bold;
        border-bottom: 1px solid #1f2d3d;
    }

    .el-menu {
        border-right: none;
    }

    .notify-item {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 8px;
    }
}

.header {
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

    .header-left {
        display: flex;
        align-items: center;

        .collapse-btn {
            font-size: 20px;
            cursor: pointer;
            margin-right: 16px;
        }
    }

    .header-right {
        display: flex;
        align-items: center;
        gap: 12px;

        .username {
            font-size: 14px;
            color: #606266;
        }

        .user-dropdown {
            cursor: pointer;
            display: flex;
            align-items: center;
        }
    }
}

.main {
    background-color: #f0f2f5;
    overflow-y: auto;
}
</style>
