package co.edu.unbosque.marketplace.compartido.error;

public record ApiError(String codigo, String mensaje, Object detalles, String correlationId) {

    public record DetalleCampo(String campo, String mensaje) {}
}
