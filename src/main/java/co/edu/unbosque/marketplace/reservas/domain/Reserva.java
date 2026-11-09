package co.edu.unbosque.marketplace.reservas.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Lectura por ahora; el modulo de Reservas se completa con sus propias HU. */
@Entity
@Table(name = "reserva")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reserva {

    @Id
    private UUID id;

    @Column(name = "huesped_id", nullable = false)
    private UUID huespedId;

    @Column(name = "publicacion_id", nullable = false)
    private UUID publicacionId;

    @Column(name = "fecha_llegada", nullable = false)
    private LocalDate fechaLlegada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "numero_huespedes", nullable = false)
    private int numeroHuespedes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoReserva estado;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private Instant fechaCreacion;
}
