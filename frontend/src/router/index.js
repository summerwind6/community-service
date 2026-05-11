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
        path: 'stat/analysis',
        name: 'StatAnalysis',
        component: () => import('@/views/stat/StatAnalysis.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis' }
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

  document.title = to.meta.title ? `${to.meta.title} - 社区便民服务系统` : '社区便民服务系统'
  next()
})

export default router
