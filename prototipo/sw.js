/* Taganga One · Service worker
   - Guarda la app para que abra sin conexión.
   - Guarda Leaflet (librería del mapa) la primera vez que se carga.
   - Guarda las teselas del mapa que el visitante ya vio (máx. ~600). */
const VERSION = "taganga-one-v1";
const APP = VERSION + "-app";
const TILES = VERSION + "-tiles";
const MAX_TILES = 600;

const APP_FILES = [
  "./",
  "./index.html",
  "./manifest.webmanifest",
  "./icons/icon-192.png",
  "./icons/icon-512.png",
  "./icons/apple-touch-icon.png"
];
const LIBS = [
  "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css",
  "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"
];

self.addEventListener("install", e => {
  e.waitUntil((async () => {
    const c = await caches.open(APP);
    await c.addAll(APP_FILES);
    // Las librerías externas no deben impedir la instalación si fallan
    await Promise.all(LIBS.map(async u => {
      try { const r = await fetch(u, { mode: "no-cors" }); await c.put(u, r); } catch (_) {}
    }));
    self.skipWaiting();
  })());
});

self.addEventListener("activate", e => {
  e.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.filter(k => !k.startsWith(VERSION)).map(k => caches.delete(k)));
    await self.clients.claim();
  })());
});

const esTesela = url =>
  url.hostname === "server.arcgisonline.com" || url.hostname.endsWith("tile.openstreetmap.fr");

async function recortar(cache) {
  const keys = await cache.keys();
  for (let i = 0; i < keys.length - MAX_TILES; i++) await cache.delete(keys[i]);
}

self.addEventListener("fetch", e => {
  const req = e.request;
  if (req.method !== "GET") return;
  const url = new URL(req.url);

  // Navegación: red primero, y si no hay conexión, la copia guardada
  if (req.mode === "navigate") {
    e.respondWith((async () => {
      try {
        const r = await fetch(req);
        (await caches.open(APP)).put("./index.html", r.clone());
        return r;
      } catch (_) {
        return (await caches.match("./index.html")) || (await caches.match("./"));
      }
    })());
    return;
  }

  // Teselas del mapa: copia guardada primero, se actualiza en segundo plano
  if (esTesela(url)) {
    e.respondWith((async () => {
      const cache = await caches.open(TILES);
      const guardada = await cache.match(req);
      const red = fetch(req).then(r => {
        if (r && (r.ok || r.type === "opaque")) { cache.put(req, r.clone()); recortar(cache); }
        return r;
      }).catch(() => guardada);
      return guardada || red;
    })());
    return;
  }

  // Archivos de la app y librerías: copia guardada primero
  if (url.origin === self.location.origin || url.hostname === "cdnjs.cloudflare.com") {
    e.respondWith((async () => {
      const guardada = await caches.match(req, { ignoreSearch: true });
      if (guardada) return guardada;
      try {
        const r = await fetch(req);
        if (r && (r.ok || r.type === "opaque")) (await caches.open(APP)).put(req, r.clone());
        return r;
      } catch (_) {
        return Response.error();
      }
    })());
  }
});
