package co.edu.unbosque.marketplace.publicaciones.presentation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Fechas en formato ISO (yyyy-MM-dd). fechaFin es inclusiva. */
public record PeriodoRequest(
        @NotNull(message = "La fecha de inicio es obligatoria") LocalDate fechaInicio,
        @NotNull(message = "La fecha de fin es obligatoria") LocalDate fechaFin) {
}
