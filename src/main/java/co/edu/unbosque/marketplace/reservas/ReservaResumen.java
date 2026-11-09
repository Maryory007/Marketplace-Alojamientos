package co.edu.unbosque.marketplace.reservas;

import java.time.LocalDate;
import java.util.UUID;

/** DTO publico del modulo Reservas. */
public record ReservaResumen(UUID id, LocalDate fechaLlegada, LocalDate fechaSalida,
                             int numeroHuespedes, String estado) {}
