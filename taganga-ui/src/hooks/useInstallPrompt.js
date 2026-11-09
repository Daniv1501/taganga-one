import { useEffect, useState } from 'react'
import { read, save } from '../utils/storage'

const DISMISSED_KEY = 'tagangaOne.sinAviso'
const DEFAULT_HINT = 'Ábrela como app y úsala sin conexión.'
const IOS_HINT = 'En Safari toca Compartir y luego «Agregar a inicio».'

function isInstalled() {
  return matchMedia('(display-mode: standalone)').matches || navigator.standalone === true
}

function isIOS() {
  return (
    /iphone|ipad|ipod/i.test(navigator.userAgent) ||
    (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1)
  )
}

export function useInstallPrompt(onToast) {
  const ios = isIOS()
  const canOffer = !isInstalled() && !read(DISMISSED_KEY, false)
  const [visible, setVisible] = useState(ios && canOffer)
  const [promptEvent, setPromptEvent] = useState(null)

  useEffect(() => {
    function handleBeforeInstall(event) {
      event.preventDefault()
      setPromptEvent(event)
      if (canOffer) setVisible(true)
    }
    function handleInstalled() {
      setVisible(false)
      onToast('Taganga One quedó instalada en tu teléfono.')
    }

    window.addEventListener('beforeinstallprompt', handleBeforeInstall)
    window.addEventListener('appinstalled', handleInstalled)
    return () => {
      window.removeEventListener('beforeinstallprompt', handleBeforeInstall)
      window.removeEventListener('appinstalled', handleInstalled)
    }
  }, [canOffer, onToast])

  async function install() {
    if (!promptEvent) return
    promptEvent.prompt()
    await promptEvent.userChoice.catch(() => {})
    setPromptEvent(null)
    setVisible(false)
  }

  function dismiss() {
    setVisible(false)
    save(DISMISSED_KEY, true)
  }

  return {
    visible,
    hint: ios ? IOS_HINT : DEFAULT_HINT,
    showButton: !ios,
    install,
    dismiss,
  }
}
