package co.edu.unbosque.marketplace.publicaciones.presentation;

import co.edu.unbosque.marketplace.publicaciones.domain.Servicio;
import java.util.UUID;

public record ServicioDto(UUID id, String nombre) {
    static ServicioDto desde(Servicio s) {
        return new ServicioDto(s.getId(), s.getNombre());
    }
}
