package co.edu.unbosque.marketplace.trazabilidad;

import java.util.UUID;

public record EventoAuditoria(UUID usuarioId, String modulo, String accion, String entidad,
                              String idEntidad, String resultado, String motivo) {

    public static final String EXITO = "EXITO";
    public static final String FALLO = "FALLO";
    public static final String DENEGADO = "DENEGADO";
}
