-- ============================================================
-- V1: Esquema inicial de Taganga One
-- ============================================================

-- Los códigos de visitante (TG-1000, TG-1001, ...) salen de una secuencia:
-- a diferencia del número aleatorio del prototipo, nunca se repiten.
CREATE SEQUENCE visitor_code_seq START WITH 1000;

CREATE TABLE visitors (
    id                BIGSERIAL PRIMARY KEY,
    visitor_code      VARCHAR(20) NOT NULL,
    full_name         VARCHAR(150) NOT NULL,
    document_type     VARCHAR(5) NOT NULL,
    document_number   VARCHAR(12) NOT NULL,
    email             VARCHAR(254) NOT NULL,
    origin            VARCHAR(120) NOT NULL,
    group_size        INTEGER NOT NULL,
    visit_date        DATE NOT NULL,
    reason            VARCHAR(30) NOT NULL,
    CONSTRAINT uk_visitors_visitor_code UNIQUE (visitor_code),
    CONSTRAINT uk_visitors_document UNIQUE (document_type, document_number),
    CONSTRAINT ck_visitors_document_type
        CHECK (document_type IN ('CC', 'CE', 'TI', 'PA')),
    CONSTRAINT ck_visitors_group_size
        CHECK (group_size BETWEEN 1 AND 50),
    CONSTRAINT ck_visitors_reason
        CHECK (reason IN (
            'TOURISM',
            'DIVING_SNORKELING',
            'ACADEMIC',
            'FAMILY_VISIT',
            'OTHER'
        ))
);

-- Prueba de la autorización de datos (Ley 1581 de 2012). Cada registro agrega
-- una fila: volver a registrarse no borra la autorización anterior.
CREATE TABLE data_consents (
    id            BIGSERIAL PRIMARY KEY,
    visitor_id    BIGINT NOT NULL,
    legal_basis   VARCHAR(100) NOT NULL,
    accepted_at   TIMESTAMP NOT NULL,
    CONSTRAINT fk_data_consents_visitor
        FOREIGN KEY (visitor_id) REFERENCES visitors (id)
);

CREATE INDEX idx_data_consents_visitor_id ON data_consents (visitor_id);

CREATE TABLE zones (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    type            VARCHAR(30) NOT NULL,
    name            VARCHAR(120) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    rule            VARCHAR(255) NOT NULL,
    geometry        VARCHAR(10) NOT NULL,
    display_order   INTEGER NOT NULL,
    CONSTRAINT uk_zones_code UNIQUE (code),
    CONSTRAINT uk_zones_display_order UNIQUE (display_order),
    CONSTRAINT ck_zones_type
        CHECK (type IN ('FREE_TRANSIT', 'SACRED', 'ECOLOGICAL', 'ARTISANAL_FISHING')),
    CONSTRAINT ck_zones_geometry
        CHECK (geometry IN ('POLYGON', 'LINE', 'POINT'))
);

CREATE TABLE zone_coordinates (
    zone_id      BIGINT NOT NULL,
    sort_order   INTEGER NOT NULL,
    latitude     DOUBLE PRECISION NOT NULL,
    longitude    DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (zone_id, sort_order),
    CONSTRAINT fk_zone_coordinates_zone
        FOREIGN KEY (zone_id) REFERENCES zones (id)
);

CREATE TABLE decalogue_rules (
    id           BIGSERIAL PRIMARY KEY,
    rule_order   INTEGER NOT NULL,
    highlight    VARCHAR(120) NOT NULL,
    body         VARCHAR(255) NOT NULL,
    CONSTRAINT uk_decalogue_rules_rule_order UNIQUE (rule_order)
);

CREATE TABLE assistant_entries (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    type            VARCHAR(10) NOT NULL,
    question        VARCHAR(200),
    answer          TEXT NOT NULL,
    display_order   INTEGER NOT NULL,
    CONSTRAINT uk_assistant_entries_code UNIQUE (code),
    CONSTRAINT ck_assistant_entries_type CHECK (type IN ('FAQ', 'EXTRA')),
    -- Una pregunta frecuente siempre tiene el texto que se muestra como sugerencia
    CONSTRAINT ck_assistant_entries_faq_question
        CHECK (type <> 'FAQ' OR question IS NOT NULL)
);

CREATE INDEX idx_assistant_entries_type ON assistant_entries (type);

CREATE TABLE assistant_keywords (
    entry_id     BIGINT NOT NULL,
    sort_order   INTEGER NOT NULL,
    keyword      VARCHAR(50) NOT NULL,
    PRIMARY KEY (entry_id, sort_order),
    CONSTRAINT fk_assistant_keywords_entry
        FOREIGN KEY (entry_id) REFERENCES assistant_entries (id)
);
