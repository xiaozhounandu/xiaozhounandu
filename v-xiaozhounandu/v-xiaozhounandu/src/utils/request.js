import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 空字符串，因为vite.config.js中配置了代理 /api -> localhost:8080
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 统一响应格式验证
    if (res.success === false) {
      console.error('API错误:', res.message)
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    if (error.response) {
      const { status, data } = error.response

      if (status === 401) {
        // token过期或未登录
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        if (window.location.pathname !== '/login') {
          alert('登录已过期，请重新登录！')
          window.location.href = '/login'
        }
      } else if (status === 403) {
        alert('无权访问该资源')
      } else if (status === 404) {
        console.error('请求的资源不存在')
      } else {
        console.error('系统错误:', data?.message || error.message)
        alert(data?.message || '系统错误，请稍后重试')
      }
    } else {
      console.error('网络错误:', error.message)
      alert('网络连接失败，请检查网络')
    }

    return Promise.reject(error)
  }
)

export default service
