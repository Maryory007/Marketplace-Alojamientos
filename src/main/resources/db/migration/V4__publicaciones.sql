CREATE TABLE politica_cancelacion (
    id                    uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre                varchar(80)   NOT NULL,
    horas_antes_llegada   integer       NOT NULL CHECK (horas_antes_llegada >= 0),
    porcentaje_devolucion numeric(5,2)  NOT NULL CHECK (porcentaje_devolucion BETWEEN 0 AND 100),
    CONSTRAINT uq_politica_nombre UNIQUE (nombre)
);

CREATE TABLE publicacion (
    id                      uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    anfitrion_id            uuid          NOT NULL REFERENCES usuario(id),
    politica_cancelacion_id uuid          NOT NULL REFERENCES politica_cancelacion(id),
    titulo                  varchar(150)  NOT NULL,
    descripcion             text          NOT NULL,
    ciudad                  varchar(80)   NOT NULL,
    ubicacion_textual       varchar(255)  NOT NULL,
    tipo                    varchar(20)   NOT NULL,
    capacidad               integer       NOT NULL CHECK (capacidad > 0),
    precio_noche            numeric(12,2) NOT NULL CHECK (precio_noche > 0),
    tarifa_limpieza         numeric(12,2) NOT NULL DEFAULT 0 CHECK (tarifa_limpieza >= 0),
    reglas                  text          NOT NULL DEFAULT '',
    modo_reserva            varchar(20)   NOT NULL,
    estado                  varchar(20)   NOT NULL,
    motivo_estado           varchar(500),
    fecha_creacion          timestamptz   NOT NULL DEFAULT now(),
    fecha_actualizacion     timestamptz   NOT NULL DEFAULT now(),
    CONSTRAINT ck_publicacion_tipo   CHECK (tipo IN ('APARTAMENTO','CASA','HABITACION','CABANA')),
    CONSTRAINT ck_publicacion_modo   CHECK (modo_reserva IN ('INMEDIATA','CON_APROBACION')),
    CONSTRAINT ck_publicacion_estado CHECK (estado IN ('BORRADOR','PENDIENTE_REVISION','ACTIVA','PAUSADA','BLOQUEADA'))
);

CREATE INDEX ix_publicacion_anfitrion ON publicacion (anfitrion_id);

-- Periodo = rango de fechas INCLUSIVO en ambos extremos ('[]').
-- La base garantiza que no se crucen (409 estructural, mismo criterio que ADR-009).
CREATE TABLE periodo_disponible (
    id             uuid  PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_id uuid  NOT NULL REFERENCES publicacion(id),
    fecha_inicio   date  NOT NULL,
    fecha_fin      date  NOT NULL,
    CONSTRAINT ck_periodo_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT ex_periodo_sin_cruce EXCLUDE USING gist (
        publicacion_id WITH =,
        daterange(fecha_inicio, fecha_fin, '[]') WITH &&
    )
);
