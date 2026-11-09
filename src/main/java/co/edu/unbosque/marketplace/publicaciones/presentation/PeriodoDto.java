package co.edu.unbosque.marketplace.publicaciones.presentation;

import co.edu.unbosque.marketplace.publicaciones.domain.PeriodoDisponible;
import java.time.LocalDate;
import java.util.UUID;

public record PeriodoDto(UUID id, UUID publicacionId, LocalDate fechaInicio, LocalDate fechaFin) {
    static PeriodoDto desde(PeriodoDisponible p) {
        return new PeriodoDto(p.getId(), p.getPublicacionId(), p.getFechaInicio(), p.getFechaFin());
    }
}
