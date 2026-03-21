import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { ThemeProvider } from './theme/ThemeContext.tsx'
import { App as AntdApp } from 'antd'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider>
      <AntdApp>
        <App />
      </AntdApp>
    </ThemeProvider>
  </StrictMode>,
)
