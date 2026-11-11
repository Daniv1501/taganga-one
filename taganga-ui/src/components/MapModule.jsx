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

// Taganga y sus alrededores: el mapa no deja perderse lejos del territorio
const AREA = L.latLngBounds([11.235, -74.235], [11.305, -74.155])

const ICONS = {
  fit: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 9V4h5M20 9V4h-5M4 15v5h5M20 15v5h-5"/><circle cx="12" cy="12" r="2.5"/></svg>',
  expand: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/></svg>',
  shrink: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 14h6v6M20 10h-6V4M14 10l7-7M3 21l7-7"/></svg>',
}

function escapeHtml(text) {
  return String(text).replace(/[&<>"']/g, (c) => `&#${c.charCodeAt(0)};`)
}

function baseStyle(zone) {
  const style = STYLES[zone.type]
  if (zone.geometry === 'LINE') return { color: style.color, weight: 4, dashArray: '2 8', lineCap: 'round' }
  if (zone.geometry === 'POINT') return { radius: 10, color: '#fff', weight: 3, fillColor: style.color, fillOpacity: 1 }
  return style
}

// Resalte de la zona bajo el mouse o seleccionada
function highlightStyle(zone) {
  const base = baseStyle(zone)
  if (zone.geometry === 'POINT') return { ...base, radius: 13 }
  if (zone.geometry === 'LINE') return { ...base, weight: 7 }
  return { ...base, weight: 4.5, fillOpacity: Math.min(base.fillOpacity + 0.2, 0.6) }
}

function buildLayer(zone) {
  const points = zone.coordinates.map((c) => [c.latitude, c.longitude])
  if (zone.geometry === 'POLYGON') return L.polygon(points, baseStyle(zone))
  if (zone.geometry === 'LINE') return L.polyline(points, baseStyle(zone))
  return L.circleMarker(points[0], baseStyle(zone))
}

// Botón propio en el estilo de los controles de Leaflet
function addButton(map, { title, icon, position = 'topleft', onClick }) {
  const control = L.control({ position })
  let button
  control.onAdd = () => {
    const bar = L.DomUtil.create('div', 'leaflet-bar map-btn-bar')
    button = L.DomUtil.create('a', 'map-btn', bar)
    button.href = '#'
    button.role = 'button'
    button.title = title
    button.setAttribute('aria-label', title)
    button.innerHTML = icon
    L.DomEvent.disableClickPropagation(bar)
    L.DomEvent.on(button, 'click', (event) => {
      L.DomEvent.preventDefault(event)
      onClick()
    })
    return bar
  }
  control.addTo(map)
  return {
    update(nextTitle, nextIcon) {
      button.title = nextTitle
      button.setAttribute('aria-label', nextTitle)
      button.innerHTML = nextIcon
    },
  }
}

function createMap(container) {
  const map = L.map(container, {
    zoomControl: false,
    // Zoom con la rueda del mouse, en pasos suaves
    scrollWheelZoom: true,
    wheelPxPerZoomLevel: 90,
    wheelDebounceTime: 30,
    zoomSnap: 0.25,
    zoomDelta: 0.5,
    minZoom: 13,
    maxZoom: 19,
    maxBounds: AREA.pad(0.5),
    maxBoundsViscosity: 0.8,
    tap: true,
  }).setView([11.2665, -74.194], 15)

  L.control.zoom({ position: 'topleft', zoomInTitle: 'Acercar', zoomOutTitle: 'Alejar' }).addTo(map)
  L.control.scale({ metric: true, imperial: false, position: 'bottomleft' }).addTo(map)

  // Teselas Esri: públicas, sin API key ni registro.
  // Nota: Esri usa el orden {z}/{y}/{x} (fila antes que columna).
  const esriAttribution = 'Tiles &copy; Esri &mdash; Esri, HERE, Garmin, USGS, OpenStreetMap contributors'
  const streets = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
    maxZoom: 19,
    attribution: esriAttribution,
  }).addTo(map)
  const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
    maxZoom: 19,
    // Esri no tiene fotos de más detalle en la zona: se amplían las de nivel 18
    maxNativeZoom: 18,
    attribution: 'Tiles &copy; Esri &mdash; Esri, Maxar, Earthstar Geographics',
  })
  L.control.layers({ Calles: streets, Satélite: satellite }, null, { position: 'topright' }).addTo(map)

  // Respaldo automático (también sin clave) si Esri falla varias veces
  let failures = 0
  streets.on('tileerror', () => {
    if (++failures !== 6) return
    map.removeLayer(streets)
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
  const layersRef = useRef(new Map())
  const boundsRef = useRef(null)
  const selectedRef = useRef(null)
  const [selected, setSelected] = useState(null)
  const [visible, setVisible] = useState({
    FREE_TRANSIT: true,
    SACRED: true,
    ECOLOGICAL: true,
    ARTISANAL_FISHING: true,
  })

  useEffect(() => {
    const container = containerRef.current
    const map = createMap(container)
    mapRef.current = map
    groupsRef.current = Object.fromEntries(
      Object.keys(STYLES).map((type) => [type, L.layerGroup()]),
    )

    addButton(map, {
      title: 'Ver todo el territorio',
      icon: ICONS.fit,
      onClick: () => {
        if (boundsRef.current) map.flyToBounds(boundsRef.current, { padding: [20, 20], duration: 0.6 })
      },
    })

    // Pantalla completa (no disponible en todos los navegadores, p. ej. iPhone)
    let fullscreenButton = null
    const handleFullscreen = () => {
      const isFull = document.fullscreenElement === container
      fullscreenButton?.update(
        isFull ? 'Salir de pantalla completa' : 'Pantalla completa',
        isFull ? ICONS.shrink : ICONS.expand,
      )
      setTimeout(() => map.invalidateSize(), 100)
    }
    if (container.requestFullscreen) {
      fullscreenButton = addButton(map, {
        title: 'Pantalla completa',
        icon: ICONS.expand,
        onClick: () => {
          if (document.fullscreenElement) document.exitFullscreen()
          else container.requestFullscreen().catch(() => {})
        },
      })
      document.addEventListener('fullscreenchange', handleFullscreen)
    }

    // Si cambia el tamaño de la ventana (p. ej. al proyectar), reajusta el mapa
    const handleResize = () => map.invalidateSize()
    window.addEventListener('resize', handleResize)
    return () => {
      window.removeEventListener('resize', handleResize)
      document.removeEventListener('fullscreenchange', handleFullscreen)
      map.remove()
      mapRef.current = null
    }
  }, [])

  // Dibuja las zonas cuando llegan de la API
  useEffect(() => {
    const map = mapRef.current
    if (!zones || !map) return undefined
    const groups = groupsRef.current
    const layers = layersRef.current

    zones.forEach((zone) => {
      const style = STYLES[zone.type]
      const layer = buildLayer(zone)
      layer.bindPopup(
        `<span class="pop-tag" style="background:${style.color}">${escapeHtml(style.label)}</span>
         <div class="pop-title">${escapeHtml(zone.name)}</div>
         <div class="pop-rule">${escapeHtml(zone.rule)}</div>`,
        { autoPanPadding: [24, 24] },
      )
      // Nombre de la zona al pasar el mouse
      layer.bindTooltip(escapeHtml(zone.name), { sticky: zone.geometry !== 'POINT', direction: 'top', className: 'zone-tip' })
      layer.on('mouseover', () => layer.setStyle(highlightStyle(zone)))
      layer.on('mouseout', () => {
        if (selectedRef.current?.code !== zone.code) layer.setStyle(baseStyle(zone))
      })
      layer.on('click', () => setSelected(zone))
      // Con el popup abierto, el nombre flotante sobra y queda tapado
      layer.on('popupopen', () => layer.closeTooltip())
      layer.addTo(groups[zone.type])
      layers.set(zone.code, { layer, zone })
    })

    // Encuadra todas las zonas, sea cual sea el tamaño del mapa
    const points = zones.flatMap((zone) => zone.coordinates.map((c) => [c.latitude, c.longitude]))
    boundsRef.current = L.latLngBounds(points)
    map.fitBounds(boundsRef.current, { padding: [20, 20], animate: false })

    return () => {
      Object.values(groups).forEach((group) => group.clearLayers())
      layers.clear()
    }
  }, [zones])

  // Resalta la zona elegida y quita el resalte de la anterior
  useEffect(() => {
    const previous = selectedRef.current
    selectedRef.current = selected
    const layers = layersRef.current
    if (previous) {
      const entry = layers.get(previous.code)
      entry?.layer.setStyle(baseStyle(entry.zone))
    }
    if (selected) {
      const entry = layers.get(selected.code)
      entry?.layer.setStyle(highlightStyle(selected))
      entry?.layer.bringToFront?.()
    }
  }, [selected])

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
            <p>Toca una zona para ver sus normas · usa la rueda del mouse o pellizca para acercar</p>
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
