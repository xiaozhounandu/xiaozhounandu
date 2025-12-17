import axios from 'axios';

// 设置axios默认baseURL
axios.defaults.baseURL = process.env.NODE_ENV === 'development' ? '' : '';

// 设置请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 设置响应拦截器
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      // token过期或无效，清除本地存储并跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 权限检查函数
export function checkAuth() {
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');

  if (!token || !user) {
    return false;
  }

  try {
    JSON.parse(user); // 验证用户数据格式
    return true;
  } catch (e) {
    return false;
  }
}

// 获取当前用户信息
export function getCurrentUser() {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch (e) {
      return null;
    }
  }
  return null;
}

// 检查用户角色
export function checkRole(role) {
  const user = getCurrentUser();
  return user && user.role === role;
}

// 检查是否是管理员
export function isAdmin() {
  return checkRole('ADMIN');
}

// 登出函数
export function logout() {
  // 调用后端登出接口
  axios.post('/api/auth/logout')
    .finally(() => {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    });
}

export default axios;