import { useEffect, useRef, useState } from 'react'
import L from 'leaflet'
import ErrorMessage from './ErrorMessage'
import { useZones } from '../hooks/useZones'

const STYLES = {
  FREE_TRANSIT: { color: '#004A87', fillColor: '#004A87', fillOpacity: 0.22, weight: 2.5, label: 'Libre tránsito turístico' },
  SACRED: { color: '#D17900', fillColor: '#D17900', fillOpacity: 0.3, weight: 2.5, label: 'Sitio sagrado / religioso' },
  ECOLOGICAL: { color: '#005C06', fillColor: '#005C06', fillOpacity: 0.25, weight: 2.5, label: 'Protección ecológica' },
  ARTISANAL_FISHING: { color: '#A3231B', fillColor: '#A3231B', fillOpacity: 0.18, weight: 2.5, dashArray: '6 6', label: 'Reserva de pesca artesanal' },
}

const FILTERS = [
  ['FREE_TRANSIT', 'Libre tránsito'],
  ['SACRED', 'Sagrado'],
  ['ECOLOGICAL', 'Ecológico'],
  ['ARTISANAL_FISHING', 'Reserva pesquera'],
]

function buildLayer(zone) {
  const style = STYLES[zone.type]
  const points = zone.coordinates.map((c) => [c.latitude, c.longitude])

  if (zone.geometry === 'POLYGON') return L.polygon(points, style)
  if (zone.geometry === 'LINE') {
    return L.polyline(points, { color: style.color, weight: 4, dashArray: '2 8', lineCap: 'round' })
  }
  return L.circleMarker(points[0], { radius: 10, color: '#fff', weight: 3, fillColor: style.color, fillOpacity: 1 })
}

function createMap(container) {
  // zoomSnap fraccionario: el encuadre aprovecha el mapa en cualquier tamaño de pantalla
  const map = L.map(container, { zoomControl: true, scrollWheelZoom: false, tap: true, zoomSnap: 0.25 })
    .setView([11.2665, -74.194], 15)

  // Teselas Esri World Street Map: públicas, sin API key ni registro.
  // Nota: Esri usa el orden {z}/{y}/{x} (fila antes que columna).
  const esri = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
    maxZoom: 19,
    attribution: 'Tiles &copy; Esri &mdash; Esri, HERE, Garmin, USGS, OpenStreetMap contributors',
  }).addTo(map)

  // Respaldo automático (también sin clave) si Esri falla varias veces
  let failures = 0
  esri.on('tileerror', () => {
    if (++failures !== 6) return
    map.removeLayer(esri)
    L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
      subdomains: 'abc',
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> · OSM France',
    }).addTo(map).bringToBack()
  })

  return map
}

export default function MapModule({ visitor, firstName, active }) {
  const { data: zones, error } = useZones()
  const containerRef = useRef(null)
  const mapRef = useRef(null)
  const groupsRef = useRef({})
  const [selected, setSelected] = useState(null)
  const [visible, setVisible] = useState({
    FREE_TRANSIT: true,
    SACRED: true,
    ECOLOGICAL: true,
    ARTISANAL_FISHING: true,
  })

  useEffect(() => {
    const map = createMap(containerRef.current)
    mapRef.current = map
    groupsRef.current = Object.fromEntries(
      Object.keys(STYLES).map((type) => [type, L.layerGroup()]),
    )

    // Si cambia el tamaño de la ventana (p. ej. al proyectar), reajusta el mapa
    const handleResize = () => map.invalidateSize()
    window.addEventListener('resize', handleResize)
    return () => {
      window.removeEventListener('resize', handleResize)
      map.remove()
      mapRef.current = null
    }
  }, [])

  // Dibuja las zonas cuando llegan de la API
  useEffect(() => {
    if (!zones || !mapRef.current) return undefined
    const groups = groupsRef.current

    zones.forEach((zone) => {
      const style = STYLES[zone.type]
      const layer = buildLayer(zone)
      layer.bindPopup(
        `<span class="pop-tag" style="background:${style.color}">${style.label}</span>
         <div class="pop-title">${zone.name}</div>
         <div class="pop-rule">${zone.rule}</div>`,
      )
      layer.on('click', () => setSelected(zone))
      layer.addTo(groups[zone.type])
    })

    // Encuadra todas las zonas, sea cual sea el tamaño del mapa
    const points = zones.flatMap((zone) => zone.coordinates.map((c) => [c.latitude, c.longitude]))
    mapRef.current.fitBounds(L.latLngBounds(points), { padding: [20, 20], animate: false })

    return () => Object.values(groups).forEach((group) => group.clearLayers())
  }, [zones])

  // Filtros de capas
  useEffect(() => {
    const map = mapRef.current
    if (!map) return
    Object.entries(groupsRef.current).forEach(([type, group]) => {
      if (visible[type]) group.addTo(map)
      else map.removeLayer(group)
    })
  }, [visible])

  // Leaflet debe recalcular su tamaño al volver a ser visible
  useEffect(() => {
    if (!active) return undefined
    const id = setTimeout(() => mapRef.current?.invalidateSize(), 50)
    return () => clearTimeout(id)
  }, [active])

  const selectedStyle = selected ? STYLES[selected.type] : null

  return (
    <div
      className={active ? 'module' : 'module hidden'}
      id="mod-mapa"
      role="tabpanel"
      aria-labelledby="nav-mapa"
    >
      <div className="welcome">
        <div style={{ flex: 1, minWidth: 0 }}>
          <h2>¡Bienvenido, {firstName}!</h2>
          <p>Muestra tu código a la autoridad comunitaria si te lo solicitan.</p>
        </div>
        <div className="welcome-badge">
          <small>Código</small>
          <b>{visitor.visitorCode}</b>
        </div>
      </div>

      <article className="card" id="sec-mapa">
        <div className="card-head">
          <div className="ico ico-azul" aria-hidden="true">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M9 4 3 6v14l6-2 6 2 6-2V4l-6 2-6-2zM9 4v14M15 6v14"/></svg>
          </div>
          <div>
            <h2>Mapa de locaciones sagradas</h2>
            <p>Toca una zona para ver sus normas</p>
          </div>
        </div>

        <div className="map-filters" role="group" aria-label="Filtrar capas del mapa">
          {FILTERS.map(([type, label]) => (
            <button
              key={type}
              className="chip"
              aria-pressed={visible[type]}
              onClick={() => setVisible((current) => ({ ...current, [type]: !current[type] }))}
            >
              <span className="dot" style={{ background: STYLES[type].color }}></span>
              {label}
            </button>
          ))}
        </div>

        <div id="map" ref={containerRef} role="application" aria-label="Mapa interactivo de Taganga"></div>

        <div className="legend" aria-hidden="true">
          <div><i style={{ background: 'rgba(0,74,135,.25)', borderColor: '#004A87' }}></i>Libre tránsito turístico</div>
          <div><i style={{ background: 'rgba(209,121,0,.3)', borderColor: '#D17900' }}></i>Sitio sagrado / religioso</div>
          <div><i style={{ background: 'rgba(0,92,6,.25)', borderColor: '#005C06' }}></i>Protección ecológica</div>
          <div><i style={{ background: 'rgba(163,35,27,.2)', borderColor: '#A3231B', borderStyle: 'dashed' }}></i>Reserva pesca artesanal</div>
        </div>

        <ErrorMessage error={error} />

        <div
          className="site-info"
          aria-live="polite"
          style={selectedStyle ? { borderLeftColor: selectedStyle.color } : undefined}
        >
          {selected ? (
            <>
              <span className="tag" style={{ background: selectedStyle.color }}>{selectedStyle.label}</span>
              <b>{selected.name}</b>
              {selected.description}<br />
              <strong>Norma:</strong> {selected.rule}
            </>
          ) : (
            <>
              <span className="tag" style={{ background: 'var(--azul)' }}>Info</span>
              <b>Selecciona una zona del mapa</b>
              Verás qué está permitido y qué está restringido en cada área.
            </>
          )}
        </div>
        <p className="map-note">⚠ Delimitación referencial para el prototipo. Los polígonos definitivos deben validarse con la comunidad y las autoridades ancestrales.</p>
      </article>
    </div>
  )
}
