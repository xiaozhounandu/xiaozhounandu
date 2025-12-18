import { createRouter, createWebHistory } from 'vue-router'
import { checkAuth, getCurrentUser } from '../utils/auth'

// 路由定义
const routes = [
  // 公开路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },

  // 需要认证的路由 (主布局)
  {
    path: '/',
    component: () => import('../views/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '数据看板', icon: '📊' }
      },
      {
        path: 'customers',
        name: 'CustomerList',
        component: () => import('../views/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: '👥' }
      },
      {
        path: 'customers/detail/:id',
        name: 'CustomerDetail',
        component: () => import('../views/customer/CustomerDetail.vue'),
        meta: { title: '客户详情', hidden: true }
      },
      {
        path: 'customers/form/:id?',
        name: 'CustomerForm',
        component: () => import('../views/customer/CustomerForm.vue'),
        meta: { title: '客户表单', hidden: true }
      },
      {
        path: 'followups',
        name: 'FollowUpList',
        component: () => import('../views/followup/FollowUpList.vue'),
        meta: { title: '跟进记录', icon: '📝' }
      },
      {
        path: 'system/logs/operations',
        name: 'OperationLog',
        component: () => import('../views/system/OperationLog.vue'),
        meta: { title: '操作日志', icon: '📋', roles: ['ADMIN'] }
      },
      {
        path: 'system/logs/login',
        name: 'LoginLog',
        component: () => import('../views/system/LoginLog.vue'),
        meta: { title: '登录日志', icon: '🔐', roles: ['ADMIN'] }
      },
      {
        path: 'user/profile',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue'),
        meta: { title: '个人中心', icon: '👤' }
      },
      {
        path: 'data-management',
        name: 'DataManagement',
        component: () => import('../views/data/DataManagement.vue'),
        meta: { title: '数据管理', icon: '🔧', roles: ['ADMIN', 'MANAGER'] }
      }
    ]
  },

  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/404/404.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active'
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = checkAuth()

  // 需要登录但未登录 -> 跳转登录页
  if (requiresAuth && !isAuthenticated) {
    next({
      path: '/login',
      query: { redirect: to.fullPath !== '/' ? to.fullPath : undefined }
    })
    return
  }

  // 已登录且访问登录/注册页 -> 跳转首页
  if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    next({ path: '/' })
    return
  }

  // 角色权限检查
  if (to.meta.roles) {
    const user = getCurrentUser()
    if (!user || !to.meta.roles.includes(user.role)) {
      alert('您没有访问该页面的权限')
      next(false)
      return
    }
  }

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 客户管理系统`
  }

  next()
})

export default router
