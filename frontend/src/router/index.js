import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/RoleManage.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'system/config',
        name: 'SystemConfig',
        component: () => import('@/views/system/ConfigView.vue'),
        meta: { title: '系统配置', icon: 'Tools' }
      },
      {
        path: 'system/session',
        name: 'SessionManage',
        component: () => import('@/views/system/SessionView.vue'),
        meta: { title: '在线用户', icon: 'Connection' }
      },
      {
        path: 'resident/list',
        name: 'ResidentManage',
        component: () => import('@/views/resident/ResidentManage.vue'),
        meta: { title: '居民管理', icon: 'User' }
      },
      {
        path: 'service/item',
        name: 'ServiceItem',
        component: () => import('@/views/service/ServiceItem.vue'),
        meta: { title: '服务项目', icon: 'Document' }
      },
      {
        path: 'apply/list',
        name: 'ApplyList',
        component: () => import('@/views/apply/ApplyList.vue'),
        meta: { title: '申请管理', icon: 'Tickets' }
      },
      {
        path: 'appointment/list',
        name: 'AppointmentManage',
        component: () => import('@/views/appointment/AppointmentManage.vue'),
        meta: { title: '预约管理', icon: 'Calendar' }
      },
      {
        path: 'notification/list',
        name: 'NotificationView',
        component: () => import('@/views/notification/NotificationView.vue'),
        meta: { title: '消息通知', icon: 'Bell' }
      },
      {
        path: 'stat/analysis',
        name: 'StatAnalysis',
        component: () => import('@/views/stat/StatAnalysis.vue'),
        meta: { title: '数据分析', icon: 'DataAnalysis' }
      },
      {
        path: 'evaluation',
        name: 'EvaluationManage',
        component: () => import('@/views/stat/EvaluationManage.vue'),
        meta: { title: '服务评价', icon: 'Star' }
      },
      {
        path: 'report',
        name: 'ReportView',
        component: () => import('@/views/stat/ReportView.vue'),
        meta: { title: '数据报表', icon: 'PieChart' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { title: '个人中心', icon: 'UserFilled' }
      },
      {
        path: 'profile/change-password',
        name: 'ChangePassword',
        component: () => import('@/views/profile/ChangePasswordView.vue'),
        meta: { title: '修改密码', icon: 'Lock' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.path === '/login') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }

  document.title = to.meta.title
    ? `${to.meta.title} - 社区便民服务系统`
    : '社区便民服务系统'
  next()
})

export default router
