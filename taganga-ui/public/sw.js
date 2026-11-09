/* Taganga One · Service worker
   - Guarda la app para que abra sin conexión.
   - Guarda las teselas del mapa que el visitante ya vio (máx. ~600).
   - Guarda la última copia de los catálogos públicos de la API (zonas, decálogo,
     preguntas frecuentes). Los registros (POST) nunca se guardan aquí. */
const VERSION = "taganga-one-v3";
const APP = VERSION + "-app";
const TILES = VERSION + "-tiles";
const API = VERSION + "-api";
const MAX_TILES = 600;

const APP_FILES = [
  "./",
  "./index.html",
  "./manifest.webmanifest",
  "./icons/icon-192.png",
  "./icons/icon-512.png",
  "./icons/apple-touch-icon.png"
];

self.addEventListener("install", e => {
  e.waitUntil((async () => {
    const c = await caches.open(APP);
    await c.addAll(APP_FILES);
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

  // Catálogos de la API: red primero, y si no hay conexión, la última copia
  if (url.origin === self.location.origin && url.pathname.startsWith("/api/")) {
    e.respondWith((async () => {
      const cache = await caches.open(API);
      try {
        const r = await fetch(req);
        if (r.ok) cache.put(req, r.clone());
        return r;
      } catch (_) {
        return (await cache.match(req)) || Response.error();
      }
    })());
    return;
  }

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

  // Archivos de la app: copia guardada primero
  if (url.origin === self.location.origin) {
    e.respondWith((async () => {
      const guardada = await caches.match(req, { ignoreSearch: true });
      if (guardada) return guardada;
      try {
        const r = await fetch(req);
        if (r && r.ok) (await caches.open(APP)).put(req, r.clone());
        return r;
      } catch (_) {
        return Response.error();
      }
    })());
  }
});
