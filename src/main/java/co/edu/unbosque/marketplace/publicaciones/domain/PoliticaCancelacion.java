package co.edu.unbosque.marketplace.publicaciones.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Datos de referencia (solo lectura). */
@Entity
@Table(name = "politica_cancelacion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PoliticaCancelacion {

    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(name = "horas_antes_llegada", nullable = false)
    private int horasAntesLlegada;

    @Column(name = "porcentaje_devolucion", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeDevolucion;
}
