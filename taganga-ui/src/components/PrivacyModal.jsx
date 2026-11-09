export default function PrivacyModal({ onAccept, onClose }) {
  return (
    <div
      className="modal"
      role="dialog"
      aria-modal="true"
      aria-labelledby="privacy-title"
      onClick={(event) => { if (event.target === event.currentTarget) onClose() }}
    >
      <div className="modal-sheet">
        <h2 id="privacy-title">Aviso de privacidad · Taganga One</h2>
        <p>En cumplimiento de la Ley Estatutaria 1581 de 2012 y el Decreto 1377 de 2013, te informamos:</p>
        <h3>Responsable</h3>
        <p>Comunidad de Taganga y sus autoridades ancestrales (Hermanos Mayores), con apoyo del equipo del proyecto Taganga One.</p>
        <h3>Finalidad</h3>
        <ul>
          <li>Registro y control ordenado del ingreso de visitantes al territorio.</li>
          <li>Estadísticas agregadas de afluencia para la gestión territorial.</li>
          <li>Envío de información de sensibilización ambiental y cultural.</li>
        </ul>
        <h3>Tus derechos como titular</h3>
        <ul>
          <li>Conocer, actualizar y rectificar tus datos.</li>
          <li>Solicitar prueba de esta autorización.</li>
          <li>Revocar la autorización y pedir la supresión de tus datos.</li>
          <li>Presentar quejas ante la Superintendencia de Industria y Comercio.</li>
        </ul>
        <h3>Lo que no haremos</h3>
        <p>No vendemos, comercializamos ni cedemos la base de datos a empresas privadas. No realizamos rastreo GPS individual.</p>
        <p style={{ marginTop: 10, fontStyle: 'italic' }}>Prototipo académico: en esta versión tus datos se envían al servidor de Taganga One y se guardan en su base de datos para el control de visitantes. En este dispositivo solo queda tu sesión abierta, que se borra al cerrar sesión o al limpiar los datos del sitio.</p>
        <button type="button" className="btn btn-primary" onClick={onAccept}>Entendido, acepto</button>
        <button type="button" className="btn btn-outline" style={{ marginTop: 10 }} onClick={onClose}>Cerrar</button>
      </div>
    </div>
  )
}
