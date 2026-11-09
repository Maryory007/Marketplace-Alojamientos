package co.edu.unbosque.marketplace.publicaciones.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Rango de fechas INCLUSIVO en ambos extremos. */
@Entity
@Table(name = "periodo_disponible")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodoDisponible {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "publicacion_id", nullable = false, updatable = false)
    private UUID publicacionId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    public PeriodoDisponible(UUID publicacionId, LocalDate fechaInicio, LocalDate fechaFin) {
        this.publicacionId = publicacionId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public void actualizar(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
