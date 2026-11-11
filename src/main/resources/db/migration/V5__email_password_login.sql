-- ============================================================
-- V5: El visitante ingresa con correo y contraseña, ya no con documento
-- ============================================================

-- Los registros anteriores no tienen contraseña y no podrían ingresar.
DELETE FROM data_consents;
DELETE FROM visitors;

ALTER TABLE visitors
    DROP CONSTRAINT uk_visitors_document,
    DROP CONSTRAINT ck_visitors_document_type,
    DROP COLUMN document_type,
    DROP COLUMN document_number,
    -- Hash BCrypt: siempre 60 caracteres
    ADD COLUMN password_hash VARCHAR(60) NOT NULL,
    ADD CONSTRAINT uk_visitors_email UNIQUE (email),
    -- El servicio guarda el correo en minúsculas para que el UNIQUE no dependa de mayúsculas
    ADD CONSTRAINT ck_visitors_email_lowercase CHECK (email = LOWER(email));
