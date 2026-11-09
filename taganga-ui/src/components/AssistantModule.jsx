import { useEffect, useRef, useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { askAssistant } from '../api/assistant'
import { useFaqs } from '../hooks/useFaqs'

const OFFLINE_ANSWER = '<p>No pude contactar el servidor para responder eso. Las preguntas frecuentes de abajo siguen disponibles sin conexión.</p>'

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

function BotMessage({ children }) {
  return (
    <div className="msg msg-bot">
      <div className="who">● Taganga One</div>
      {children}
    </div>
  )
}

export default function AssistantModule({ firstName, active }) {
  const { data: faqs } = useFaqs()
  const ask = useMutation({ mutationFn: askAssistant })
  const [messages, setMessages] = useState([])
  const [busy, setBusy] = useState(false)
  const [text, setText] = useState('')
  const logRef = useRef(null)
  const nextId = useRef(0)

  // Mantiene visible el último mensaje; display:none deja el scroll en 0 al ocultarse
  useEffect(() => {
    if (active && logRef.current) logRef.current.scrollTop = logRef.current.scrollHeight
  }, [messages, busy, active])

  function addMessage(role, content) {
    setMessages((current) => [...current, { id: nextId.current++, role, content }])
  }

  async function send(question, fixedAnswer) {
    if (busy) return
    setBusy(true)
    addMessage('user', question)

    let answer = fixedAnswer
    if (!answer) {
      try {
        const result = await ask.mutateAsync({ question, visitorName: firstName })
        answer = result.answer
      } catch {
        answer = OFFLINE_ANSWER
      }
    }

    // Simula "pensar", igual que el prototipo
    await wait(600 + Math.min(1200, answer.length * 2))
    addMessage('bot', answer)
    setBusy(false)
  }

  function handleSubmit(event) {
    event.preventDefault()
    const question = text.trim()
    if (!question || busy) return
    setText('')
    send(question)
  }

  return (
    <div
      className={active ? 'module' : 'module hidden'}
      id="mod-asistente"
      role="tabpanel"
      aria-labelledby="nav-asistente"
    >
      <article className="card chat" id="sec-asistente">
        <div className="card-head">
          <div className="ico ico-verde" aria-hidden="true">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/><path d="M8 10h.01M12 10h.01M16 10h.01"/></svg>
          </div>
          <div style={{ flex: 1 }}>
            <h2>Asistente Virtual Taganga One</h2>
            <div className="chat-status">En línea · IA simulada</div>
          </div>
        </div>

        <div className="chat-log" ref={logRef} aria-live="polite" aria-label="Conversación con el asistente">
          <BotMessage>
            <p>¡Hola, {firstName}! Soy <b>Taganga One</b>, tu asistente virtual. 🌿</p>
            <p>Toca una pregunta frecuente o escríbeme lo que quieras saber sobre el territorio.</p>
          </BotMessage>

          {messages.map((message) =>
            message.role === 'user' ? (
              <div key={message.id} className="msg msg-user">{message.content}</div>
            ) : (
              <BotMessage key={message.id}>
                {/* HTML de la base de conocimiento; el backend escapa el nombre del visitante */}
                <div dangerouslySetInnerHTML={{ __html: message.content }} />
              </BotMessage>
            ),
          )}

          {busy && (
            <div className="msg msg-bot typing" aria-label="El asistente está escribiendo">
              <span></span><span></span><span></span>
            </div>
          )}
        </div>

        <div className="suggest-label">Preguntas frecuentes</div>
        <div className="suggestions">
          {faqs?.map((faq) => (
            <button key={faq.code} type="button" onClick={() => send(faq.question, faq.answer)}>
              {faq.question}
            </button>
          ))}
        </div>

        <form className="chat-bar" autoComplete="off" onSubmit={handleSubmit}>
          <label htmlFor="chat-input" className="hidden">Escribe tu pregunta</label>
          <input
            id="chat-input"
            type="text"
            placeholder="Escribe tu pregunta…"
            maxLength={300}
            enterKeyHint="send"
            value={text}
            onChange={(e) => setText(e.target.value)}
          />
          <button type="submit" className="send-btn" aria-label="Enviar mensaje" disabled={busy}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"><path d="M22 2 11 13M22 2l-7 20-4-9-9-4 20-7z"/></svg>
          </button>
        </form>
      </article>
    </div>
  )
}
