package co.edu.unbosque.marketplace.publicaciones.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Fila de la tabla servicio_adicional (el id lo genera la base). Se reemplaza completa en cada edicion. */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicioAdicionalPublicacion {

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo;

    public ServicioAdicionalPublicacion(String nombre, BigDecimal costo) {
        this.nombre = nombre;
        this.costo = costo;
    }
}
