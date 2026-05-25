import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Vite proxies /api requests to the Spring Boot backend during dev,
// so the React app can call relative URLs and avoid CORS in development.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
