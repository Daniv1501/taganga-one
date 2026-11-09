import { useEffect, useState } from 'react'
import AuthScreen from './components/AuthScreen'
import MainScreen from './components/MainScreen'
import OfflinePill from './components/OfflinePill'
import Toast from './components/Toast'
import { useInstallPrompt } from './hooks/useInstallPrompt'
import { useToast } from './hooks/useToast'
import { read, save } from './utils/storage'
import { scrollToTop } from './utils/scroll'

const SESSION_KEY = 'tagangaOne.visitante'

export default function App() {
  const [visitor, setVisitor] = useState(() => read(SESSION_KEY, null))
  const [authMode, setAuthMode] = useState('registro')
  const { message, showToast } = useToast()
  const install = useInstallPrompt(showToast)

  useEffect(scrollToTop, [visitor])

  function handleEnter(entered) {
    save(SESSION_KEY, entered)
    setVisitor(entered)
  }

  function handleLogout() {
    save(SESSION_KEY, null)
    setVisitor(null)
    setAuthMode('ingreso')
    showToast('Sesión cerrada. Puedes volver a ingresar con tu documento.')
  }

  return (
    <>
      <div className="app" id="app">
        {visitor ? (
          <MainScreen visitor={visitor} onLogout={handleLogout} />
        ) : (
          <AuthScreen
            initialMode={authMode}
            install={install}
            onEnter={handleEnter}
            onToast={showToast}
          />
        )}
      </div>
      <Toast message={message} />
      <OfflinePill />
    </>
  )
}
