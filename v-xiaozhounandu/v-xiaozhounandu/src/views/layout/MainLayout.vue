<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h1>客户管理系统</h1>
      </div>
      <nav class="sidebar-nav">
        <ul>
          <li v-for="item in menuItems" :key="item.path">
            <router-link
              :to="item.path"
              class="nav-link"
              :class="{ active: $route.path === item.path || $route.path.startsWith(item.path) }"
              v-if="item.visible"
            >
              <span class="nav-icon">{{ item.icon }}</span>
              <span class="nav-text">{{ item.title }}</span>
            </router-link>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <button class="toggle-btn" @click="toggleSidebar">
            {{ sidebarCollapsed ? '☰' : '✕' }}
          </button>
        </div>
        <div class="header-right">
          <div class="user-info" v-if="currentUser">
            <span class="welcome">👋 欢迎,</span>
            <span class="username">{{ currentUser.nickname || currentUser.username }}</span>
            <span class="role-badge" :class="currentUser.role.toLowerCase()">{{ getRoleLabel(currentUser.role) }}</span>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="content-wrapper">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser, logout, isManager, isAdmin } from '../../utils/auth'

const router = useRouter()
const sidebarCollapsed = ref(false)
const currentUser = ref(null)

// 动态菜单项
const menuItems = computed(() => [
  {
    path: '/',
    title: '数据看板',
    icon: '📊',
    visible: true
  },
  {
    path: '/customers',
    title: '客户管理',
    icon: '👥',
    visible: true
  },
  {
    path: '/followups',
    title: '跟进记录',
    icon: '📝',
    visible: true
  },
  {
    path: '/system/logs/operations',
    title: '操作日志',
    icon: '📋',
    visible: isAdmin()
  },
  {
    path: '/system/logs/login',
    title: '登录日志',
    icon: '🔐',
    visible: isAdmin()
  },
  {
    path: '/user/profile',
    title: '个人中心',
    icon: '👤',
    visible: true
  },
  {
    path: '/data-management',
    title: '数据管理',
    icon: '🔧',
    visible: isAdmin() || isManager()
  }
])

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

async function handleLogout() {
  if (confirm('确定要退出登录吗？')) {
    await logout()
  }
}

function getRoleLabel(role) {
  const map = {
    'ADMIN': '管理员',
    'MANAGER': '销售经理',
    'USER': '用户'
  }
  return map[role] || role
}

onMounted(() => {
  currentUser.value = getCurrentUser()
})
</script>

<style scoped>
/* 主布局 */
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: #f5f7fa;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.sidebar.collapsed {
  width: 0;
  overflow: hidden;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.sidebar-nav ul {
  list-style: none;
  margin: 0;
  padding: 10px 0;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border-left-color: #3498db;
  font-weight: 600;
}

.nav-icon {
  margin-right: 12px;
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 14px;
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航 */
.header {
  height: 60px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.toggle-btn:hover {
  background: #2980b9;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.welcome {
  color: #606266;
}

.username {
  color: #303133;
  font-weight: 600;
}

.role-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.role-badge.admin {
  background: #fef0f0;
  color: #f56c6c;
}

.role-badge.manager {
  background: #fdf6ec;
  color: #e6a23c;
}

.role-badge.user {
  background: #eef2f7;
  color: #606266;
}

.logout-btn {
  background: #f56c6c;
  color: white;
  border: none;
  padding: 6px 14px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
  margin-left: 8px;
}

.logout-btn:hover {
  background: #dd5f5f;
}

/* 内容区域 */
.content-wrapper {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 20px;
}
</style>
