package co.edu.unbosque.marketplace.trazabilidad.infrastructure;

import co.edu.unbosque.marketplace.compartido.CorrelationId;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
class AuditoriaJdbc implements AuditoriaPort {

    private final JdbcClient jdbc;

    AuditoriaJdbc(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(EventoAuditoria e) {
        jdbc.sql("""
                INSERT INTO audit_event
                    (usuario_id, modulo, accion, entidad, id_entidad, resultado, motivo, id_correlacion)
                VALUES
                    (:usuarioId, :modulo, :accion, :entidad, :idEntidad, :resultado, :motivo, :correlacion)
                """)
                .param("usuarioId", e.usuarioId())
                .param("modulo", e.modulo())
                .param("accion", e.accion())
                .param("entidad", e.entidad())
                .param("idEntidad", e.idEntidad())
                .param("resultado", e.resultado())
                .param("motivo", recortar(e.motivo(), 500))
                .param("correlacion", CorrelationId.actualONuevo())
                .update();
    }

    private static String recortar(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
