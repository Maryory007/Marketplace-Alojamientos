package co.edu.unbosque.marketplace.compartido.error;

public class ExcepcionNegocio extends RuntimeException {
    private final TipoError tipo;
    private final String codigo;
    private final transient Object detalles;

    public ExcepcionNegocio(TipoError tipo, String codigo, String mensaje) {
        this(tipo, codigo, mensaje, null);
    }

    public ExcepcionNegocio(TipoError tipo, String codigo, String mensaje, Object detalles) {
        super(mensaje);
        this.tipo = tipo;
        this.codigo = codigo;
        this.detalles = detalles;
    }

    public TipoError getTipo() { return tipo; }
    public String getCodigo() { return codigo; }
    public Object getDetalles() { return detalles; }
}
