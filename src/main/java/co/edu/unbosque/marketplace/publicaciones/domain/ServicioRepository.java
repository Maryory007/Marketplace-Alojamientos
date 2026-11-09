package co.edu.unbosque.marketplace.publicaciones.domain;

import java.util.List;
import java.util.UUID;

public interface ServicioRepository {
    List<Servicio> findAll();
    List<Servicio> findAllById(Iterable<UUID> ids);
}
