<template>
  <div class="not-found-container">
    <div class="error-card">
      <div class="error-icon">⚠️</div>
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在或已被移除</p>
      <div class="actions">
        <button class="btn-primary" @click="goHome">返回首页</button>
        <button class="btn-secondary" @click="goBack">返回上一页</button>
      </div>
      <div class="tips">
        <p>💡 提示：</p>
        <ul>
          <li>检查URL是否输入正确</li>
          <li>页面可能已被移动或删除</li>
          <li>联系管理员确认页面状态</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}

function goBack() {
  router.back()
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.error-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  max-width: 480px;
  width: 100%;
}

.error-icon {
  font-size: 80px;
  margin-bottom: 16px;
  animation: bounce 2s infinite;
}

h1 {
  margin: 0 0 8px 0;
  font-size: 72px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: #34495e;
}

p {
  margin: 0 0 24px 0;
  color: #7f8c8d;
  font-size: 16px;
  line-height: 1.6;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
}

.tips {
  text-align: left;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.tips p {
  margin: 0 0 8px 0;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.tips ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
  font-size: 13px;
  line-height: 1.8;
}

.tips li {
  margin-bottom: 4px;
}

.tips li:last-child {
  margin-bottom: 0;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

/* 响应式 */
@media (max-width: 480px) {
  .error-card {
    padding: 30px 20px;
  }

  h1 {
    font-size: 56px;
  }

  h2 {
    font-size: 20px;
  }

  .actions {
    flex-direction: column;
  }

  .btn-primary, .btn-secondary {
    width: 100%;
  }
}
</style>
