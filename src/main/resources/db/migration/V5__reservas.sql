CREATE TABLE reserva (
    id                      uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    huesped_id              uuid         NOT NULL REFERENCES usuario(id),
    publicacion_id          uuid         NOT NULL REFERENCES publicacion(id),
    politica_cancelacion_id uuid         NOT NULL REFERENCES politica_cancelacion(id),
    fecha_llegada           date         NOT NULL,
    fecha_salida            date         NOT NULL,
    numero_huespedes        integer      NOT NULL CHECK (numero_huespedes > 0),
    estado                  varchar(25)  NOT NULL,
    clave_idempotencia      varchar(80)  NOT NULL,
    fecha_creacion          timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT ck_reserva_fechas CHECK (fecha_salida > fecha_llegada),
    CONSTRAINT ck_reserva_estado CHECK (estado IN (
        'PENDIENTE_PAGO','PENDIENTE_APROBACION','CONFIRMADA','EN_REVISION',
        'COMPLETADA','CANCELADA','PAGO_FALLIDO')),
    CONSTRAINT uq_reserva_idempotencia UNIQUE (huesped_id, clave_idempotencia),
    -- ADR-009: cero superposiciones; CANCELADA y PAGO_FALLIDO liberan el periodo.
    -- Noches ocupadas = [llegada, salida): el dia de salida queda libre.
    CONSTRAINT ex_reserva_sin_superposicion EXCLUDE USING gist (
        publicacion_id WITH =,
        daterange(fecha_llegada, fecha_salida, '[)') WITH &&
    ) WHERE (estado IN ('PENDIENTE_PAGO','PENDIENTE_APROBACION','CONFIRMADA','EN_REVISION','COMPLETADA'))
);

CREATE INDEX ix_reserva_publicacion_fechas ON reserva (publicacion_id, fecha_llegada, fecha_salida);
CREATE INDEX ix_reserva_huesped ON reserva (huesped_id);
