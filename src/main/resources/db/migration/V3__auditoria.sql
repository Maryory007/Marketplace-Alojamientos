-- ADR-011: auditoria de solo insercion
CREATE TABLE audit_event (
    id             bigserial    PRIMARY KEY,
    fecha          timestamptz  NOT NULL DEFAULT now(),
    usuario_id     uuid         REFERENCES usuario(id),
    modulo         varchar(40)  NOT NULL,
    accion         varchar(60)  NOT NULL,
    entidad        varchar(40)  NOT NULL,
    id_entidad     varchar(60),
    resultado      varchar(20)  NOT NULL,
    motivo         varchar(500),
    id_correlacion uuid         NOT NULL
);

CREATE INDEX ix_audit_usuario     ON audit_event (usuario_id);
CREATE INDEX ix_audit_correlacion ON audit_event (id_correlacion);
CREATE INDEX ix_audit_fecha       ON audit_event (fecha);

CREATE FUNCTION audit_event_inmutable() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'audit_event es de solo insercion (% no permitido)', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_event_no_update_delete
    BEFORE UPDATE OR DELETE ON audit_event
    FOR EACH ROW EXECUTE FUNCTION audit_event_inmutable();

CREATE TRIGGER trg_audit_event_no_truncate
    BEFORE TRUNCATE ON audit_event
    FOR EACH STATEMENT EXECUTE FUNCTION audit_event_inmutable();
