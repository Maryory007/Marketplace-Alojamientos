-- Catalogo de servicios, servicios de cada publicacion, servicios adicionales con costo e imagenes.
CREATE TABLE servicio (
    id     uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre varchar(80) NOT NULL,
    CONSTRAINT uq_servicio_nombre UNIQUE (nombre)
);

CREATE TABLE publicacion_servicio (
    publicacion_id uuid NOT NULL REFERENCES publicacion(id),
    servicio_id    uuid NOT NULL REFERENCES servicio(id),
    PRIMARY KEY (publicacion_id, servicio_id)
);

CREATE TABLE servicio_adicional (
    id             uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_id uuid          NOT NULL REFERENCES publicacion(id),
    nombre         varchar(80)   NOT NULL,
    costo          numeric(12,2) NOT NULL CHECK (costo >= 0)
);
CREATE INDEX ix_servicio_adicional_publicacion ON servicio_adicional (publicacion_id);

CREATE TABLE imagen (
    id             uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_id uuid         NOT NULL REFERENCES publicacion(id),
    ruta           varchar(500) NOT NULL,
    orden          integer      NOT NULL CHECK (orden >= 0)
);
CREATE INDEX ix_imagen_publicacion ON imagen (publicacion_id);

INSERT INTO servicio (nombre) VALUES
  ('WiFi'), ('Cocina'), ('Parqueadero'), ('Piscina'), ('Aire acondicionado'),
  ('Televisor'), ('Lavadora'), ('Admite mascotas'), ('Desayuno incluido'), ('Gimnasio');
