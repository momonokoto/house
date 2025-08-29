import axios from 'axios'

const request = axios.create({
  baseURL: '/api',    // 改为代理路径前缀
  timeout: 5000
})

// 请求拦截器：带上 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

export default request
