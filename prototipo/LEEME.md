# Taganga One · Aplicación web

## Qué contiene
- `index.html`: la aplicación
- `manifest.webmanifest`: nombre, colores e íconos para instalarla
- `sw.js`: permite que abra sin conexión y guarda el mapa ya visto
- `icons/`: íconos de la app

## Cómo publicarla gratis (5 minutos)
**Opción fácil: Netlify Drop**
1. Entra a https://app.netlify.com/drop
2. Arrastra la carpeta `prototipo` completa (descomprimida).
3. Te da un enlace `https://….netlify.app`. Ese es el enlace de tu app.

**Opción GitHub Pages**
1. Crea un repositorio y sube todos los archivos.
2. Settings → Pages → Branch `main` → Save.

Tiene que ser un sitio **https** para que se pueda instalar y funcione sin conexión.
Abrir `index.html` con doble clic sirve para verla, pero no para instalarla.

## Cómo instalarla en el teléfono
- **Android (Chrome):** abre el enlace y toca «Instalar» en el aviso dorado (o menú ⋮ → «Instalar app»).
- **iPhone (Safari):** botón Compartir → «Agregar a inicio».

## Notas
- Los registros de visitantes quedan guardados **solo en ese teléfono**. No hay servidor central todavía.
- El mapa necesita Internet la primera vez. Las zonas que el visitante ya vio quedan guardadas para usarlas sin señal.
- Si cambias la app, sube la versión en `sw.js` (`taganga-one-v1` → `v2`) para que los teléfonos la actualicen.
