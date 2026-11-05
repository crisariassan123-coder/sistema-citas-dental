import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Todo lo que empiece con /api se reenvía al backend de Spring Boot
      '/api': 'http://localhost:8080',
    },
  },
})
