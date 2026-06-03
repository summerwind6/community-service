import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const roles = ref(JSON.parse(localStorage.getItem('userRoles') || '[]'))
  const permissions = ref(JSON.parse(localStorage.getItem('userPermissions') || '[]'))

  async function login(loginForm) {
    try {
      const res = await loginApi(loginForm)
      token.value = res.data.token || 'session-token'
      userInfo.value = res.data.user
      roles.value = res.data.user.roles || []
      permissions.value = res.data.user.permissions || []
      localStorage.setItem('token', token.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      localStorage.setItem('userRoles', JSON.stringify(roles.value))
      localStorage.setItem('userPermissions', JSON.stringify(permissions.value))
      return res
    } catch (error) {
      throw error
    }
  }

  async function getUserInfo() {
    try {
      const res = await getUserInfoApi()
      userInfo.value = res.data
      roles.value = res.data.roles || []
      permissions.value = res.data.permissions || []
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      localStorage.setItem('userRoles', JSON.stringify(roles.value))
      localStorage.setItem('userPermissions', JSON.stringify(permissions.value))
      return res
    } catch (error) {
      throw error
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = {}
      roles.value = []
      permissions.value = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('userRoles')
      localStorage.removeItem('userPermissions')
    }
  }

  function hasRole(role) {
    return roles.value.includes(role)
  }

  function hasPermission(permission) {
    return permissions.value.includes(permission)
  }

  function hasAnyPermission(...perms) {
    return perms.some(p => permissions.value.includes(p))
  }

  return {
    token,
    userInfo,
    roles,
    permissions,
    login,
    getUserInfo,
    logout,
    hasRole,
    hasPermission,
    hasAnyPermission
  }
})
