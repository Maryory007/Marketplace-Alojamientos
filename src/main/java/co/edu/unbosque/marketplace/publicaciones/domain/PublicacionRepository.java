package co.edu.unbosque.marketplace.publicaciones.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicacionRepository {
    Optional<Publicacion> findById(UUID id);
    <S extends Publicacion> S save(S publicacion);
    List<Publicacion> findByAnfitrionIdOrderByFechaActualizacionDesc(UUID anfitrionId);
}
