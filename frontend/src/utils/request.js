import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    if (response.config?.responseType === 'blob' || response.config?.responseType === 'arraybuffer') {
      return response.data
    }

    const res = response.data
    
    if (res.code !== 200) {
      ElMessage.error(res.msg || '操作失败')
      
      if (res.code === 401) {
        ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
        })
      }
      
      return Promise.reject(new Error(res.msg || '操作失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误：', error)
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          message = '拒绝访问，权限不足'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 429:
        case 503:
          message = error.response.data?.msg || '服务繁忙，请稍后重试'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `连接错误${error.response.status}`
      }
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
