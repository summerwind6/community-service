import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const roles = ref([])

  async function login(loginForm) {
    try {
      const res = await loginApi(loginForm)
      token.value = res.data.token || 'session-token'
      userInfo.value = res.data.user
      localStorage.setItem('token', token.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
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
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
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
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }

  function hasRole(role) {
    return roles.value.includes(role)
  }

  function hasPermission(permission) {
    return userInfo.value.permissions?.includes(permission)
  }

  return {
    token,
    userInfo,
    roles,
    login,
    getUserInfo,
    logout,
    hasRole,
    hasPermission
  }
})
