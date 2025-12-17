import { createRouter, createWebHistory } from 'vue-router'
import { checkAuth } from '../utils/auth'

// 引入你的页面组件
import HelloWorld from '../components/HelloWorld.vue'
import Customer from '../view/Customer.vue'
import Login from '../view/Login.vue'
import datas from "../components/datas.vue";

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/',
        name: 'Customer',
        component: Customer,
        meta: { requiresAuth: true }
    },
    {
        path: '/hello',
        name: 'HelloWorld',
        component: HelloWorld,
        meta: { requiresAuth: true }
    },
    {
        path: '/2',
        name: 'datas',
        component: datas,
        meta: { requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫：检查权限
router.beforeEach((to, from, next) => {
    // 检查该路由是否需要登录权限
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 判断用户是否已登录
        if (!checkAuth()) {
            // 未登录，跳转到登录页面
            next({
                path: '/login',
                // 保存原本要去的路径，登录成功后跳转
                query: { redirect: to.fullPath }
            })
        } else {
            // 已登录，允许访问
            next()
        }
    } else {
        // 不需要登录权限的页面直接访问
        // 如果已经登录且访问登录页面，则跳转到首页
        if (to.path === '/login' && checkAuth()) {
            next({ path: '/' })
        } else {
            next()
        }
    }
})

export default router
