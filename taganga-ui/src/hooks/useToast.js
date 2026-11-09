import { useCallback, useEffect, useRef, useState } from 'react'

export function useToast() {
  const [message, setMessage] = useState(null)
  const timer = useRef(null)

  const showToast = useCallback((text) => {
    clearTimeout(timer.current)
    setMessage(text)
    timer.current = setTimeout(() => setMessage(null), 3200)
  }, [])

  useEffect(() => () => clearTimeout(timer.current), [])

  return { message, showToast }
}
