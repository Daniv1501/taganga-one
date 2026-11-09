import ErrorMessage from './ErrorMessage'
import { useDecalogue } from '../hooks/useDecalogue'

export default function DecalogueModule({ active }) {
  const { data: rules, isLoading, error } = useDecalogue()

  return (
    <div
      className={active ? 'module' : 'module hidden'}
      id="mod-decalogo"
      role="tabpanel"
      aria-labelledby="nav-decalogo"
    >
      <article className="card decalogo" id="sec-decalogo">
        <div className="card-head">
          <div className="ico ico-dorado" aria-hidden="true">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 2 4 5v6c0 5 3.4 9.4 8 11 4.6-1.6 8-6 8-11V5l-8-3z"/><path d="m9 12 2 2 4-4"/></svg>
          </div>
          <div>
            <h2>Decálogo del visitante</h2>
            <p>Reglas de conducta en el territorio ancestral</p>
          </div>
        </div>
        {isLoading && <p className="loading">Cargando…</p>}
        <ErrorMessage error={error} />
        <ol>
          {rules?.map((rule) => (
            <li key={rule.ruleOrder}>
              <span><b>{rule.highlight}</b> {rule.body}</span>
            </li>
          ))}
        </ol>
        <div className="decalogo-foot">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
          Al registrarte te comprometiste a cumplir este decálogo.
        </div>
      </article>
    </div>
  )
}
