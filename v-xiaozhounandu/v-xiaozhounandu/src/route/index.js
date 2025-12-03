import { createRouter, createWebHistory } from 'vue-router'

// 引入你的页面组件
import HelloWorld from '../components/HelloWorld.vue'
import Customer from '../view/Customer.vue'

const routes = [
    {
        path: '/',
        name: 'Customer',
        component: Customer
    },
    {
        path: '/hello',
        name: 'HelloWorld',
        component: HelloWorld
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
