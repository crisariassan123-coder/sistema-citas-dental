import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import PanelApp from './panel/PanelApp.jsx'

// Se elige qué aplicación mostrar según la ruta: /panel para el personal, cualquier otra para el paciente
const esPanel = window.location.pathname.startsWith('/panel')

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {esPanel ? <PanelApp /> : <App />}
  </StrictMode>,
)
