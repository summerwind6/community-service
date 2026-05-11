<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    userStore.getUserInfo()
  }
})
</script>

<style>
#app {
  width: 100%;
  height: 100vh;
}
</style>
