# Taganga One

## 1. Nombre del proyecto

**Taganga One** — salvaguarda territorial, sensibilización turística y control de visitantes
del territorio ancestral de Taganga (Santa Marta, Colombia).

## 2. Descripción breve

Aplicación web instalable (PWA) con tres módulos para el visitante: **mapa territorial**,
**asistente virtual** y **decálogo de conducta**, precedidos por un registro con autorización
de tratamiento de datos (Ley 1581 de 2012).

Parte del prototipo HTML que se conserva en [`prototipo/`](prototipo/), reconstruido con la
arquitectura de DeepBlue Rescue:

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA / Hibernate, MapStruct, Flyway y
  PostgreSQL, con pruebas de integración reales mediante Testcontainers.
- **Frontend:** [`taganga-ui/`](taganga-ui/), con React, Vite, TanStack Query y Leaflet.

El asistente es **simulado**: responde por coincidencia de palabras clave, sin IA real.

## 3. Modelo de datos

| Entidad | Tabla | Descripción |
|---|---|---|
| `Visitor` | `visitors` | Visitante registrado (código, nombre, correo, hash de la contraseña, procedencia, personas, fecha, motivo) |
| `DataConsent` | `data_consents` | Prueba de cada autorización de datos aceptada (norma, fecha y hora) |
| `Zone` | `zones` | Zona del mapa (tipo, nombre, descripción, norma, geometría) |
| `GeoPoint` | `zone_coordinates` | Coordenadas ordenadas de una zona (`@Embeddable`) |
| `DecalogueRule` | `decalogue_rules` | Regla del decálogo |
| `AssistantEntry` | `assistant_entries` + `assistant_keywords` | Respuesta del asistente y sus palabras clave |

Enumerados (persistidos como `VARCHAR` vía `@Enumerated(EnumType.STRING)`):

- `VisitReason`: `TOURISM`, `DIVING_SNORKELING`, `ACADEMIC`, `FAMILY_VISIT`, `OTHER`
- `ZoneType`: `FREE_TRANSIT`, `SACRED`, `ECOLOGICAL`, `ARTISANAL_FISHING`
- `ZoneGeometry`: `POLYGON`, `LINE`, `POINT`
- `AssistantEntryType`: `FAQ`, `EXTRA`

## 4. Relaciones

```
Visitor (1) ──── (N) DataConsent

Zone (1) ──── (N) GeoPoint                 (@ElementCollection ordenada)

AssistantEntry (1) ──── (N) keyword        (@ElementCollection ordenada)
```

| Relación | Tipo | Lado dueño de la FK | Cascade |
|---|---|---|---|
| `Visitor` → `DataConsent` | 1:N | `DataConsent` (`visitor_id`) | `ALL` desde `Visitor.consents` |

`DecalogueRule` es un catálogo independiente, sin relaciones.

## 5. API REST

Los errores usan siempre el mismo contrato (`ErrorResponse`): `timestamp`, `status`, `error`,
`message` y `details`.

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| `POST` | `/api/visitors` | Registrar visitante | `201`, `400`, `409` |
| `POST` | `/api/visitors/login` | Ingresar con correo y contraseña | `200`, `400`, `401` |
| `GET` | `/api/zones` | Zonas del mapa con sus coordenadas | `200` |
| `GET` | `/api/decalogue` | Reglas del decálogo en orden | `200` |
| `GET` | `/api/assistant/faqs` | Preguntas frecuentes con su respuesta | `200` |
| `POST` | `/api/assistant/questions` | Preguntar al asistente | `200`, `400` |

**Reglas de negocio**

1. La fecha de visita no puede ser anterior a hoy, según la hora de Colombia (`409`).
2. Un correo tiene un solo registro. Registrarlo de nuevo con la **misma contraseña** reemplaza
   los datos y genera un código nuevo; con otra contraseña se rechaza (`409`).
3. Cada registro agrega una fila en `data_consents`, así que el historial de autorizaciones
   se conserva aunque se reemplacen los datos.
4. Sin autorización de datos no hay registro (`400`).
5. La contraseña tiene entre 8 y 72 caracteres (máximo 72 bytes, el límite de BCrypt) y se
   guarda solo como hash BCrypt. El correo se guarda en minúsculas.
6. Si el correo o la contraseña no coinciden, el ingreso responde `401` con el mismo mensaje
   en ambos casos, para no revelar qué correos están registrados.

`VisitorResponse` nunca devuelve el correo ni la contraseña.

## 6. Instrucciones para ejecutar la aplicación

Requiere PostgreSQL con una base de datos llamada `taganga`:

```bash
createdb -U postgres taganga
```

Por defecto se conecta a `jdbc:postgresql://localhost:5432/taganga` con usuario y clave
`postgres`; se puede sobreescribir con `DB_URL`, `DB_USER` y `DB_PASSWORD`.

**Backend** (puerto 8080):

```bash
mvn spring-boot:run
```

**Frontend** (puerto 5173, reenvía `/api` al backend):

```bash
cd taganga-ui
npm install
npm run dev
```

El service worker solo se activa en el build, para probarlo:

```bash
npm run build
npm run preview
```

## 7. Instrucciones para ejecutar los tests

```bash
mvn clean test
```

No se requiere una base de datos local, pero **Docker debe estar corriendo**: Testcontainers
levanta un contenedor con PostgreSQL para `PersistenceIntegrationTest`.

## 8. Flyway

Flyway es el único responsable del esquema. Hibernate nunca genera DDL (`ddl-auto: validate`):
al arrancar solo comprueba que las entidades coincidan con las tablas.

| Migración | Contenido |
|---|---|
| `V1__create_schema.sql` | Tablas, restricciones `UNIQUE` y `CHECK`, índices y la secuencia de códigos |
| `V2__insert_zones.sql` | Las 10 zonas del mapa y sus 43 coordenadas |
| `V3__insert_decalogue.sql` | Las 10 reglas del decálogo |
| `V4__insert_assistant_knowledge.sql` | 6 preguntas frecuentes, 7 respuestas adicionales y 85 palabras clave |
| `V5__email_password_login.sql` | Quita el documento, agrega `password_hash` y vuelve único el correo. **Borra los visitantes anteriores**, que no tenían contraseña |

## 9. Diferencias con el prototipo

| Tema | Prototipo | Esta versión |
|---|---|---|
| Vista en computador | Dentro de un teléfono simulado | Adaptable a celular, tablet y computador, sin marco |
| Datos de visitantes | Solo en el teléfono | En el servidor (PostgreSQL) |
| Aviso de privacidad | «No se envían a ningún servidor» | Reescrito para decir que se envían al servidor |
| Código de visitante | Aleatorio de 4 dígitos, podía repetirse | Secuencia de la base de datos, no se repite |
| Autorización de datos | Se sobrescribía al registrarse de nuevo | Se conserva el historial |
| Zonas, decálogo y asistente | Escritos en el JavaScript | En la base de datos |
| Leaflet | CDN | Dependencia npm, incluida en el build |
| Sin conexión | Todo funcionaba | Abre la app, el mapa ya visto, el decálogo y las preguntas frecuentes; **registrarse, ingresar y hacer preguntas libres requieren conexión** |

## 10. Limitaciones conocidas

Antes de usarlo con visitantes reales hay que resolver lo siguiente:

- **No se verifica el correo**: cualquiera puede registrarse con un correo ajeno que aún no esté
  registrado.
- **No hay recuperación de contraseña.**
- **No hay sesión en el servidor ni límite de intentos** de ingreso: la API no emite token y
  la sesión solo se recuerda en el navegador.
- **Faltan los derechos del titular** que promete el aviso: la app no permite consultar,
  rectificar ni suprimir datos, ni revocar la autorización.
- **Los polígonos del mapa son referenciales** y deben validarse con la comunidad y las
  autoridades ancestrales.
- **El contenido cultural e histórico** viene tal cual del prototipo y también debería
  validarse con la comunidad.

## Estructura del proyecto

```
taganga-one/
├── pom.xml
├── README.md
├── prototipo/             (HTML original, sin servidor)
├── src/main
│   ├── java/com/taganga/one
│   │   ├── TagangaOneApplication.java
│   │   ├── config/        (reloj en hora de Colombia)
│   │   ├── controller/    (REST)
│   │   ├── domain/        (entidades JPA + enums)
│   │   ├── dto/           (request / response)
│   │   ├── exception/     (ErrorResponse común)
│   │   ├── mapper/        (MapStruct)
│   │   ├── repository/    (JpaRepository)
│   │   └── service/       (interfaces + impl)
│   └── resources
│       ├── application.yml
│       └── db/migration   (V1, V2, V3, V4)
├── src/test/java/com/taganga/one
│   ├── PersistenceIntegrationTest.java
│   ├── controller/
│   └── service/
└── taganga-ui/
    ├── public/            (manifest, sw.js, íconos)
    └── src
        ├── api/
        ├── components/
        ├── hooks/
        ├── utils/
        ├── App.jsx
        └── main.jsx
```
