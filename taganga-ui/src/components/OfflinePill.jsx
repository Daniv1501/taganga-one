import { useEffect, useState } from 'react'

export default function OfflinePill() {
  const [online, setOnline] = useState(navigator.onLine)

  useEffect(() => {
    const update = () => setOnline(navigator.onLine)
    window.addEventListener('online', update)
    window.addEventListener('offline', update)
    return () => {
      window.removeEventListener('online', update)
      window.removeEventListener('offline', update)
    }
  }, [])

  if (online) return null

  return (
    <div className="offline-pill" role="status">
      Sin conexión · el mapa muestra solo lo ya visto
    </div>
  )
}
