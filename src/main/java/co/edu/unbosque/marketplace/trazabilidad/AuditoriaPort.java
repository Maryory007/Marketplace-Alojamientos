package co.edu.unbosque.marketplace.trazabilidad;

public interface AuditoriaPort {
    /** Se escribe en una transaccion independiente: un rollback de la operacion no borra el registro. */
    void registrar(EventoAuditoria evento);
}
