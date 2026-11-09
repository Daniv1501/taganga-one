export default function InstallBar({ install }) {
  if (!install.visible) return null

  return (
    <div className="install-bar">
      <img src="/icons/icon-192.png" alt="" />
      <div className="txt">
        <strong>Instala Taganga One</strong>
        <span>{install.hint}</span>
      </div>
      {install.showButton && (
        <button type="button" className="btn-inst" onClick={install.install}>Instalar</button>
      )}
      <button type="button" className="x" aria-label="Cerrar aviso" onClick={install.dismiss}>&times;</button>
    </div>
  )
}
