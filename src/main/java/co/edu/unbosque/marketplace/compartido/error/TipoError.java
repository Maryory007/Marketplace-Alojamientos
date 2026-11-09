package co.edu.unbosque.marketplace.compartido.error;

public enum TipoError {
    SOLICITUD_INVALIDA(400),
    NO_AUTENTICADO(401),
    PROHIBIDO(403),
    NO_ENCONTRADO(404),
    CONFLICTO(409),
    REGLA_NEGOCIO(422);

    private final int estadoHttp;

    TipoError(int estadoHttp) { this.estadoHttp = estadoHttp; }

    public int getEstadoHttp() { return estadoHttp; }
}
