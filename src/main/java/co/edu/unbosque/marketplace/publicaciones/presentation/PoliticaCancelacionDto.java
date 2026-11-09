package co.edu.unbosque.marketplace.publicaciones.presentation;

import co.edu.unbosque.marketplace.publicaciones.domain.PoliticaCancelacion;
import java.math.BigDecimal;
import java.util.UUID;

public record PoliticaCancelacionDto(UUID id, String nombre, int horasAntesLlegada,
                                     BigDecimal porcentajeDevolucion) {
    static PoliticaCancelacionDto desde(PoliticaCancelacion p) {
        return new PoliticaCancelacionDto(p.getId(), p.getNombre(), p.getHorasAntesLlegada(),
                p.getPorcentajeDevolucion());
    }
}
