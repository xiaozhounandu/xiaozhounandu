import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
    plugins: [
        vue(),
        vueDevTools(),
    ],

    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080', // 必须加 http://
                changeOrigin: true,
                // rewrite: path => path.replace(/^\/api/, '') // 可选
            }
        }
    }
})
