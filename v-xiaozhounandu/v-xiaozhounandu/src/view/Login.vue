<template>
  <div class="login-container">
    <div class="login-card">
      <h2>客户管理系统</h2>
      <h3>用户登录</h3>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="loginForm.username"
            placeholder="请输入用户名"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            placeholder="请输入密码"
            required
          />
        </div>

        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="demo-info">
        <p>测试账号：</p>
        <p>管理员 - 用户名: admin, 密码: admin123</p>
        <p>普通用户 - 用户名: user, 密码: user123</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loading: false
    };
  },
  methods: {
    async handleLogin() {
      this.loading = true;

      try {
        const response = await axios.post('/api/auth/login', this.loginForm);

        if (response.data.success) {
          // 保存token和用户信息
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('user', JSON.stringify(response.data.user));

          // 设置axios默认headers
          axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;

          alert('登录成功！');

          // 跳转到原本要访问的页面或首页
          const redirect = this.$route.query.redirect || '/';
          this.$router.push(redirect);
        } else {
          alert(response.data.message || '登录失败');
        }
      } catch (error) {
        console.error('登录错误:', error);
        alert('登录失败，请检查网络连接');
      } finally {
        this.loading = false;
      }
    }
  },
  mounted() {
    // 如果已经登录，直接跳转到首页
    const token = localStorage.getItem('token');
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: Arial, sans-serif;
}

.login-card {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.login-card h2 {
  text-align: center;
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.login-card h3 {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
  font-size: 20px;
  font-weight: normal;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: bold;
  color: #555;
}

.form-group input {
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.login-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.demo-info {
  margin-top: 30px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 5px;
  font-size: 14px;
  color: #666;
}

.demo-info p {
  margin: 5px 0;
}
</style>