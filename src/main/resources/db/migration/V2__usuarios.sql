CREATE TABLE usuario (
    id               uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre           varchar(120) NOT NULL,
    correo           varchar(160) NOT NULL,
    telefono         varchar(30),
    contrasena_hash  varchar(255) NOT NULL,
    rol              varchar(20)  NOT NULL,
    estado_cuenta    varchar(20)  NOT NULL,
    fecha_registro   timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('HUESPED','ANFITRION','ADMINISTRADOR')),
    CONSTRAINT ck_usuario_estado CHECK (estado_cuenta IN ('ACTIVA','DESACTIVADA','RESTRINGIDA'))
);
