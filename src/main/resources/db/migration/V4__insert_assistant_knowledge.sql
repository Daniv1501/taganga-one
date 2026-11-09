-- ============================================================
-- V4: Base de conocimiento del asistente (datos del prototipo HTML).
--  - FAQ:   preguntas frecuentes; gana la que tenga más palabras clave.
--  - EXTRA: se revisan en orden solo si ninguna FAQ coincidió.
-- {visitor} se reemplaza por ", <nombre>" en el saludo.
-- ============================================================

INSERT INTO assistant_entries (code, type, question, answer, display_order) VALUES
('sagrados', 'FAQ', '¿Cuáles son los sitios sagrados de Taganga?',
 '<p>En el mapa están señalados en <b style="color:#D17900">dorado</b>. Los principales son:</p>
        <ul><li><b>Cerro tutelar:</b> lugar de pagamento y memoria de los Hermanos Mayores. Solo se entra con autorización.</li>
        <li><b>Capilla de San Pedro:</b> templo del patrono de los pescadores.</li>
        <li><b>Punta de los ancestros:</b> promontorio rocoso de valor espiritual.</li></ul>
        <p>En todos ellos: silencio, no extraer elementos naturales y no dejar ofrendas ajenas a la tradición.</p>', 1),
('ambiente', 'FAQ', '¿Cuáles son las normas de protección ambiental?',
 '<p>Las normas clave son:</p>
        <ul><li>No tocar ni extraer corales, conchas, plantas o animales.</li>
        <li>Llevarte tus residuos; evita plásticos de un solo uso.</li>
        <li>Usar protector solar biodegradable.</li>
        <li>Nada de fogatas ni tala en el bosque seco tropical.</li>
        <li>Buceo y snorkel solo con operadores registrados.</li></ul>
        <p>Puedes verlas completas en el <b>Decálogo</b>.</p>', 2),
('historia', 'FAQ', '¿Cuál es la historia comunitaria?',
 '<p>Taganga es uno de los asentamientos más antiguos del Caribe colombiano. Su población reconoce raíces en los pueblos <b>tayrona</b> de la Sierra Nevada, y su vida ha girado por siglos en torno a la <b>pesca artesanal</b> y la relación espiritual con el mar y los cerros.</p>
        <p>Con el crecimiento del turismo, la comunidad y sus autoridades ancestrales buscan ordenar las visitas para proteger el territorio. Taganga One nace en ese propósito.</p>', 3),
('pesca', 'FAQ', '¿Qué son las zonas de reserva pesquera artesanal?',
 '<p>Son áreas de la bahía (en <b style="color:#A3231B">rojo punteado</b> en el mapa) donde la comunidad cala redes y pesca de forma tradicional.</p>
        <ul><li>Prohibido el paso de motos acuáticas y lanchas recreativas.</li>
        <li>No nadar ni anclar dentro de la zona.</li>
        <li>Mantén 50 m de distancia de las embarcaciones de pesca.</li>
        <li>No manipules redes, boyas ni nasas.</li></ul>', 4),
('datos', 'FAQ', '¿Cómo se usan mis datos personales?',
 '<p>Tus datos se tratan conforme a la <b>Ley 1581 de 2012</b> y el Decreto 1377 de 2013, solo para el control de visitantes por la comunidad y las autoridades ancestrales.</p>
        <p>Puedes conocer, actualizar, rectificar o pedir la supresión de tus datos en cualquier momento. <b>No se venden ni se ceden</b> a empresas privadas y no hay rastreo GPS.</p>', 5),
('llegar', 'FAQ', '¿Cómo llego a Playa Grande?',
 '<p>Hay dos formas:</p>
        <ul><li><b>A pie:</b> por el sendero costero que sale del extremo sur del malecón (unos 20–30 min). Ve con agua, calzado cerrado y no salgas del camino.</li>
        <li><b>En lancha:</b> desde la playa principal, con lancheros locales.</li></ul>
        <p>Está marcada en <b style="color:#004A87">azul</b> en el mapa como zona de libre tránsito.</p>', 6),
('saludo', 'EXTRA', NULL,
 '<p>¡Hola{visitor}! 👋 Soy el asistente de Taganga One. Pregúntame por sitios sagrados, normas ambientales, pesca artesanal o la historia de la comunidad.</p>', 7),
('gracias', 'EXTRA', NULL,
 '<p>¡Con gusto! Disfruta Taganga con respeto. 🌊</p>', 8),
('fotos', 'EXTRA', NULL,
 '<p>Puedes tomar fotos en zonas de libre tránsito. Pide permiso antes de fotografiar personas o ceremonias, y no vueles drones sobre sitios sagrados ni zonas de pesca sin autorización comunitaria.</p>', 9),
('guias', 'EXTRA', NULL,
 '<p>Contrata guías y operadores de buceo <b>locales y registrados</b> ante la comunidad. Así tu visita beneficia directamente a Taganga y se respetan las zonas protegidas.</p>', 10),
('emergencia', 'EXTRA', NULL,
 '<p>En caso de emergencia llama a la <b>línea 123</b>. También puedes acudir a la autoridad comunitaria en la playa principal.</p>', 11),
('cobros', 'EXTRA', NULL,
 '<p>Taganga One <b>no cobra</b> por el registro ni realiza recaudos de peaje. Es una herramienta de sensibilización y control territorial.</p>', 12),
('acampar', 'EXTRA', NULL,
 '<p>No está permitido acampar ni hacer fogatas en cerros, puntas o playas no habilitadas. Consulta hospedajes locales en el pueblo.</p>', 13);

INSERT INTO assistant_keywords (entry_id, sort_order, keyword)
SELECT e.id, k.ord - 1, k.keyword
FROM (VALUES
    ('sagrados', ARRAY['sagrad','sitio','cerro','ancestr','capilla','templo','religio','pagamento']),
    ('ambiente', ARRAY['ambient','norma','regla','proteg','basura','coral','arrecife','fauna','bosque','residuo','plastic']),
    ('historia', ARRAY['histor','origen','comunidad','pueblo','tayrona','cultura','tradic','fundac']),
    ('pesca',    ARRAY['pesca','pescad','red','chinchorro','lancha','moto acu','jet','bahía','bahia','navega']),
    ('datos',    ARRAY['dato','privacidad','habeas','1581','informaci','registro','borrar','suprim']),
    ('llegar',   ARRAY['llegar','playa grande','sendero','camin','ruta','como voy','cómo voy']),
    ('saludo',   ARRAY['hola','buenas','buenos','hey','saludo']),
    ('gracias',  ARRAY['gracias','graciass','thank']),
    ('fotos',    ARRAY['foto','fotograf','dron']),
    ('guias',    ARRAY['guía','guia','tour','operador','buceo','snorkel']),
    ('emergencia', ARRAY['emergencia','ayuda','policía','policia','accidente','herido']),
    ('cobros',   ARRAY['cobro','pagar','precio','tarifa','cuesta','peaje']),
    ('acampar',  ARRAY['acampar','camping','fogata','carpa'])
) AS source (entry_code, keywords)
CROSS JOIN LATERAL unnest(source.keywords) WITH ORDINALITY AS k (keyword, ord)
JOIN assistant_entries e ON e.code = source.entry_code;
