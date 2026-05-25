import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { UnitsProvider } from './units'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <UnitsProvider>
      <App />
    </UnitsProvider>
  </React.StrictMode>,
)
