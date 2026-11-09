-- ============================================================
-- V2: Zonas del mapa territorial (datos del prototipo HTML).
-- Delimitación referencial: los polígonos definitivos deben
-- validarse con la comunidad y las autoridades ancestrales.
-- ============================================================

INSERT INTO zones (code, type, name, description, rule, geometry, display_order) VALUES
('PLAYA_MALECON', 'FREE_TRANSIT', 'Playa y malecón de Taganga',
 'Zona principal de llegada, comercio local y baño.',
 'Permitido: baño, recorridos y comercio local. Respeta las lanchas varadas y las faenas de pesca.',
 'POLYGON', 1),
('PLAYA_GRANDE', 'FREE_TRANSIT', 'Playa Grande',
 'Playa turística accesible por sendero o lancha.',
 'Permitido: baño y descanso. Llévate tus residuos; no hay recolección en la playa.',
 'POLYGON', 2),
('SENDERO_PLAYA_GRANDE', 'FREE_TRANSIT', 'Sendero Taganga – Playa Grande',
 'Camino peatonal costero.',
 'Transita solo por el sendero marcado. No abras caminos nuevos en el cerro.',
 'LINE', 3),
('CERRO_TUTELAR', 'SACRED', 'Cerro tutelar (sitio ancestral)',
 'Lugar de pagamento y memoria de los Hermanos Mayores.',
 'Acceso restringido. Solo con autorización y acompañamiento de la autoridad ancestral. Prohibido extraer piedras o vegetación.',
 'POLYGON', 4),
('CAPILLA_SAN_PEDRO', 'SACRED', 'Capilla de San Pedro (religioso)',
 'Templo del patrono de los pescadores.',
 'Visita en silencio y con vestimenta adecuada. No interrumpas celebraciones.',
 'POINT', 5),
('PUNTA_ANCESTROS', 'SACRED', 'Punta de los ancestros',
 'Promontorio rocoso de valor espiritual.',
 'No acampar, no hacer fogatas, no dejar ofrendas ajenas a la tradición local.',
 'POLYGON', 6),
('BOSQUE_SECO', 'ECOLOGICAL', 'Bosque seco tropical (cerros)',
 'Ecosistema frágil con fauna endémica.',
 'Prohibido talar, cazar, encender fuego o alimentar fauna. Ingreso solo con guía local.',
 'POLYGON', 7),
('ARRECIFE_PRADERA', 'ECOLOGICAL', 'Arrecife y pradera marina',
 'Zona de corales y pastos marinos.',
 'Snorkel y buceo solo con operador registrado. No pisar ni tocar corales. Usa protector solar biodegradable.',
 'POLYGON', 8),
('RESERVA_BAHIA_NORTE', 'ARTISANAL_FISHING', 'Reserva de pesca artesanal (bahía norte)',
 'Área de calado de redes y chinchorro de la comunidad.',
 'Prohibido el paso de motos acuáticas, lanchas recreativas y bañistas. No manipular redes ni boyas.',
 'POLYGON', 9),
('CALADERO_BAHIA_SUR', 'ARTISANAL_FISHING', 'Caladero tradicional (bahía sur)',
 'Zona de pesca de línea y nasa.',
 'Mantén distancia de 50 m de las embarcaciones de pesca. No anclar.',
 'POLYGON', 10);

INSERT INTO zone_coordinates (zone_id, sort_order, latitude, longitude)
SELECT z.id, c.sort_order, c.latitude, c.longitude
FROM (VALUES
    ('PLAYA_MALECON', 0, 11.2692, -74.1918),
    ('PLAYA_MALECON', 1, 11.2690, -74.1898),
    ('PLAYA_MALECON', 2, 11.2660, -74.1893),
    ('PLAYA_MALECON', 3, 11.2648, -74.1905),
    ('PLAYA_MALECON', 4, 11.2662, -74.1920),

    ('PLAYA_GRANDE', 0, 11.2618, -74.2005),
    ('PLAYA_GRANDE', 1, 11.2612, -74.1982),
    ('PLAYA_GRANDE', 2, 11.2590, -74.1975),
    ('PLAYA_GRANDE', 3, 11.2584, -74.1996),
    ('PLAYA_GRANDE', 4, 11.2600, -74.2010),

    ('SENDERO_PLAYA_GRANDE', 0, 11.2650, -74.1915),
    ('SENDERO_PLAYA_GRANDE', 1, 11.2640, -74.1935),
    ('SENDERO_PLAYA_GRANDE', 2, 11.2628, -74.1955),
    ('SENDERO_PLAYA_GRANDE', 3, 11.2615, -74.1980),

    ('CERRO_TUTELAR', 0, 11.2735, -74.1940),
    ('CERRO_TUTELAR', 1, 11.2745, -74.1905),
    ('CERRO_TUTELAR', 2, 11.2720, -74.1885),
    ('CERRO_TUTELAR', 3, 11.2705, -74.1915),
    ('CERRO_TUTELAR', 4, 11.2712, -74.1940),

    ('CAPILLA_SAN_PEDRO', 0, 11.2672, -74.1882),

    ('PUNTA_ANCESTROS', 0, 11.2720, -74.2010),
    ('PUNTA_ANCESTROS', 1, 11.2728, -74.1985),
    ('PUNTA_ANCESTROS', 2, 11.2710, -74.1975),
    ('PUNTA_ANCESTROS', 3, 11.2700, -74.1998),

    ('BOSQUE_SECO', 0, 11.2650, -74.1885),
    ('BOSQUE_SECO', 1, 11.2660, -74.1860),
    ('BOSQUE_SECO', 2, 11.2620, -74.1855),
    ('BOSQUE_SECO', 3, 11.2595, -74.1900),
    ('BOSQUE_SECO', 4, 11.2612, -74.1925),
    ('BOSQUE_SECO', 5, 11.2635, -74.1905),

    ('ARRECIFE_PRADERA', 0, 11.2632, -74.2030),
    ('ARRECIFE_PRADERA', 1, 11.2640, -74.2005),
    ('ARRECIFE_PRADERA', 2, 11.2622, -74.1998),
    ('ARRECIFE_PRADERA', 3, 11.2612, -74.2022),

    ('RESERVA_BAHIA_NORTE', 0, 11.2712, -74.1975),
    ('RESERVA_BAHIA_NORTE', 1, 11.2715, -74.1935),
    ('RESERVA_BAHIA_NORTE', 2, 11.2695, -74.1925),
    ('RESERVA_BAHIA_NORTE', 3, 11.2680, -74.1950),
    ('RESERVA_BAHIA_NORTE', 4, 11.2690, -74.1975),

    ('CALADERO_BAHIA_SUR', 0, 11.2650, -74.1965),
    ('CALADERO_BAHIA_SUR', 1, 11.2655, -74.1935),
    ('CALADERO_BAHIA_SUR', 2, 11.2638, -74.1940),
    ('CALADERO_BAHIA_SUR', 3, 11.2630, -74.1962)
) AS c (zone_code, sort_order, latitude, longitude)
JOIN zones z ON z.code = c.zone_code;
