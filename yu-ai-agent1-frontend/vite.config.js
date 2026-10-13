import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  // 依赖均可直接以 ESM 加载；关闭预打包也可避开部分 Windows 受限目录下的扫描问题。
  optimizeDeps: {
    noDiscovery: true,
    include: [],
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8123',
        changeOrigin: true,
      },
    },
  },
})
