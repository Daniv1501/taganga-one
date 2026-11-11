import { useEffect, useRef, useState } from 'react'
import BrandLogo from './BrandLogo'
import ErrorMessage from './ErrorMessage'
import InstallBar from './InstallBar'
import PrivacyModal from './PrivacyModal'
import { useLoginVisitor } from '../hooks/useLoginVisitor'
import { useRegisterVisitor } from '../hooks/useRegisterVisitor'

const REASONS = [
  ['TOURISM', 'Turismo'],
  ['DIVING_SNORKELING', 'Buceo / snorkel'],
  ['ACADEMIC', 'Académico'],
  ['FAMILY_VISIT', 'Visita familiar'],
  ['OTHER', 'Otro'],
]

function todayIso() {
  const now = new Date()
  return new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 10)
}

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/

// BCrypt cuenta bytes: una tilde o una ñ ocupan dos
function isPasswordValid(password) {
  return password.length >= 8 && new TextEncoder().encode(password).length <= 72
}

// Mismas reglas del prototipo; el backend las vuelve a validar
function validateRegistration(form) {
  const name = form.fullName.trim()
  const group = parseInt(form.groupSize, 10)
  const invalid = new Set()

  if (!(name.split(/\s+/).length >= 2 && name.length >= 5)) invalid.add('fullName')
  if (form.origin.trim().length < 2) invalid.add('origin')
  if (!(group >= 1 && group <= 50)) invalid.add('groupSize')
  if (!EMAIL_PATTERN.test(form.email.trim())) invalid.add('email')
  if (!isPasswordValid(form.password)) invalid.add('password')
  if (form.passwordConfirm !== form.password) invalid.add('passwordConfirm')
  if (!form.visitDate) invalid.add('visitDate')
  if (!form.consent) invalid.add('consent')

  return invalid
}

export default function AuthScreen({ initialMode, install, onEnter, onToast }) {
  const minDate = todayIso()
  const [mode, setMode] = useState(initialMode)
  const [form, setForm] = useState({
    fullName: '',
    origin: '',
    groupSize: '1',
    email: '',
    password: '',
    passwordConfirm: '',
    visitDate: minDate,
    reason: 'TOURISM',
    consent: false,
  })
  const [invalid, setInvalid] = useState(() => new Set())
  const [focusRequest, setFocusRequest] = useState(0)
  const [privacyOpen, setPrivacyOpen] = useState(false)

  const formRef = useRef(null)
  const loginTabRef = useRef(null)
  const register = useRegisterVisitor()
  const login = useLoginVisitor()
  const pending = register.isPending || login.isPending
  const serverError = mode === 'registro' ? register.error : login.error

  // Lleva el foco al primer campo con error, una vez pintado
  useEffect(() => {
    if (focusRequest === 0) return
    const bad = formRef.current?.querySelector(
      '.field.invalid input, .field.invalid select, .consent.invalid input',
    )
    if (bad) {
      bad.focus()
      bad.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  }, [focusRequest])

  function changeMode(next) {
    setMode(next)
    setInvalid(new Set())
    register.reset()
    login.reset()
  }

  function setField(name, value) {
    setForm((current) => ({ ...current, [name]: value }))
    setInvalid((current) => {
      if (!current.has(name)) return current
      const next = new Set(current)
      next.delete(name)
      return next
    })
  }

  const fieldClass = (name) => (invalid.has(name) ? 'field invalid' : 'field')

  function showErrors(errors) {
    setInvalid(errors)
    setFocusRequest((n) => n + 1)
  }

  function handleSubmit(event) {
    event.preventDefault()
    if (pending) return

    if (mode === 'ingreso') {
      const errors = new Set()
      if (!EMAIL_PATTERN.test(form.email.trim())) errors.add('email')
      if (!form.password) errors.add('password')
      if (errors.size > 0) return showErrors(errors)
      setInvalid(new Set())
      login.mutate(
        { email: form.email.trim(), password: form.password },
        { onSuccess: onEnter },
      )
      return
    }

    const errors = validateRegistration(form)
    if (errors.size > 0) return showErrors(errors)

    setInvalid(new Set())
    register.mutate(
      {
        fullName: form.fullName.trim(),
        origin: form.origin.trim(),
        groupSize: parseInt(form.groupSize, 10),
        email: form.email.trim(),
        password: form.password,
        visitDate: form.visitDate,
        reason: form.reason,
        dataConsentAccepted: form.consent,
      },
      {
        onSuccess: onEnter,
        onError: (error) => {
          // El correo ya existe con otra contraseña: lo más probable es que deba ingresar
          if (error.status !== 409 || !error.message.includes('correo')) return
          onToast(error.message)
          changeMode('ingreso')
          loginTabRef.current?.focus()
        },
      },
    )
  }

  return (
    <section id="screen-auth" aria-label="Registro de visitantes">
      <header className="auth-hero">
        <div className="brand">
          <BrandLogo size={34} />
          <div>
            <div className="brand-name">Taganga <span>One</span></div>
            <div className="brand-sub">Territorio ancestral · Santa Marta</div>
          </div>
        </div>
        <h1>Bienvenido al territorio ancestral de Taganga</h1>
        <p>Regístrate antes de tu visita. Tu registro apoya el control territorial de la comunidad y las autoridades ancestrales.</p>
      </header>

      <InstallBar install={install} />

      <div className="auth-card">
        <div className="segmented" role="tablist" aria-label="Tipo de acceso">
          <button
            type="button"
            role="tab"
            aria-selected={mode === 'registro'}
            onClick={() => changeMode('registro')}
          >
            Registrarme
          </button>
          <button
            type="button"
            role="tab"
            ref={loginTabRef}
            aria-selected={mode === 'ingreso'}
            onClick={() => changeMode('ingreso')}
          >
            Ya me registré
          </button>
        </div>

        <form ref={formRef} noValidate onSubmit={handleSubmit}>
          {mode === 'registro' && (
            <div className={fieldClass('fullName')}>
              <label htmlFor="f-nombre">Nombre completo</label>
              <input
                id="f-nombre"
                type="text"
                autoComplete="name"
                placeholder="Ej. María Pérez Gómez"
                required
                value={form.fullName}
                onChange={(e) => setField('fullName', e.target.value)}
              />
              <div className="err">Escribe tu nombre completo.</div>
            </div>
          )}

          <div className={fieldClass('email')}>
            <label htmlFor="f-correo">Correo electrónico</label>
            <input
              id="f-correo"
              type="email"
              autoComplete="email"
              inputMode="email"
              placeholder="tucorreo@ejemplo.com"
              required
              value={form.email}
              onChange={(e) => setField('email', e.target.value)}
            />
            <div className="err">Escribe un correo válido.</div>
          </div>

          <div className={fieldClass('password')}>
            <label htmlFor="f-clave">Contraseña</label>
            <input
              id="f-clave"
              type="password"
              autoComplete={mode === 'registro' ? 'new-password' : 'current-password'}
              placeholder={mode === 'registro' ? 'Mínimo 8 caracteres' : ''}
              required
              value={form.password}
              onChange={(e) => setField('password', e.target.value)}
            />
            <div className="err">
              {mode === 'registro' ? 'La contraseña debe tener entre 8 y 72 caracteres.' : 'Escribe tu contraseña.'}
            </div>
          </div>

          {mode === 'registro' && (
            <div className={fieldClass('passwordConfirm')}>
              <label htmlFor="f-clave2">Confirmar contraseña</label>
              <input
                id="f-clave2"
                type="password"
                autoComplete="new-password"
                required
                value={form.passwordConfirm}
                onChange={(e) => setField('passwordConfirm', e.target.value)}
              />
              <div className="err">Las contraseñas no coinciden.</div>
            </div>
          )}

          {mode === 'registro' && (
            <div>
              <div className="row-2">
                <div className={fieldClass('origin')}>
                  <label htmlFor="f-procedencia">Procedencia</label>
                  <input
                    id="f-procedencia"
                    type="text"
                    placeholder="Ciudad / país"
                    required
                    value={form.origin}
                    onChange={(e) => setField('origin', e.target.value)}
                  />
                  <div className="err">Indica tu procedencia.</div>
                </div>
                <div className={fieldClass('groupSize')}>
                  <label htmlFor="f-grupo">Personas</label>
                  <input
                    id="f-grupo"
                    type="number"
                    inputMode="numeric"
                    min="1"
                    max="50"
                    required
                    value={form.groupSize}
                    onChange={(e) => setField('groupSize', e.target.value)}
                  />
                  <div className="err">Entre 1 y 50.</div>
                </div>
              </div>

              <div className="row-2">
                <div className={fieldClass('visitDate')}>
                  <label htmlFor="f-fecha">Fecha de visita</label>
                  <input
                    id="f-fecha"
                    type="date"
                    required
                    min={minDate}
                    value={form.visitDate}
                    onChange={(e) => setField('visitDate', e.target.value)}
                  />
                  <div className="err">Elige una fecha.</div>
                </div>
                <div className="field">
                  <label htmlFor="f-motivo">Motivo</label>
                  <select
                    id="f-motivo"
                    value={form.reason}
                    onChange={(e) => setField('reason', e.target.value)}
                  >
                    {REASONS.map(([value, label]) => (
                      <option key={value} value={value}>{label}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* AUTORIZACIÓN OBLIGATORIA LEY 1581 DE 2012 */}
              <div className={invalid.has('consent') ? 'consent invalid' : 'consent'}>
                <label className="consent-check" htmlFor="f-consent">
                  <input
                    type="checkbox"
                    id="f-consent"
                    required
                    aria-describedby="consent-desc"
                    checked={form.consent}
                    onChange={(e) => setField('consent', e.target.checked)}
                  />
                  <span className="consent-text" id="consent-desc">
                    <strong>Autorizo de manera previa, expresa e informada</strong> el tratamiento de mis datos personales conforme a la <strong>Ley 1581 de 2012</strong> (Habeas Data) y el Decreto 1377 de 2013, con la finalidad exclusiva de control de visitantes por parte de la comunidad y las autoridades ancestrales de Taganga.{' '}
                    <button
                      type="button"
                      className="link-btn"
                      onClick={(e) => {
                        e.preventDefault()
                        e.stopPropagation()
                        setPrivacyOpen(true)
                      }}
                    >
                      Leer aviso de privacidad
                    </button>
                  </span>
                </label>
                <div className="err" role="alert">Debes aceptar la autorización de tratamiento de datos para continuar.</div>
              </div>
            </div>
          )}

          <ErrorMessage error={serverError} />

          <button type="submit" className="btn btn-primary" disabled={pending}>
            <span>{mode === 'registro' ? 'Registrarme e ingresar' : 'Ingresar'}</span>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
          </button>
        </form>
      </div>

      <p className="auth-foot">Tus datos <b>no se venden ni se ceden</b> a terceros. Sin cobros, sin pasarelas de pago y sin rastreo GPS.</p>

      {privacyOpen && (
        <PrivacyModal
          onClose={() => setPrivacyOpen(false)}
          onAccept={() => {
            setField('consent', true)
            setPrivacyOpen(false)
          }}
        />
      )}
    </section>
  )
}
