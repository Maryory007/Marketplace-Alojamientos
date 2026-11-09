-- Enlaces de restablecimiento de contrasena. Se guarda solo el hash SHA-256 del token.
CREATE TABLE recuperacion_contrasena (
    id             uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id     uuid         NOT NULL REFERENCES usuario(id),
    token_hash     varchar(64)  NOT NULL,
    expira_en      timestamptz  NOT NULL,
    usado_en       timestamptz,
    fecha_creacion timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT uq_recuperacion_token UNIQUE (token_hash)
);

CREATE INDEX ix_recuperacion_usuario ON recuperacion_contrasena (usuario_id);
