package co.edu.unbosque.marketplace.compartido;

import java.util.UUID;
import org.slf4j.MDC;

public final class CorrelationId {
    public static final String CLAVE_MDC = "correlationId";
    public static final String HEADER = "X-Correlation-Id";

    private CorrelationId() {}

    public static String actual() {
        return MDC.get(CLAVE_MDC);
    }

    public static UUID actualONuevo() {
        String valor = actual();
        try {
            return valor != null ? UUID.fromString(valor) : UUID.randomUUID();
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }
}
