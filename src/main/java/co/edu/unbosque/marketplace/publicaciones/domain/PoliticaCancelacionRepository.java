package co.edu.unbosque.marketplace.publicaciones.domain;

import java.util.List;
import java.util.UUID;

public interface PoliticaCancelacionRepository {
    List<PoliticaCancelacion> findAll();
    boolean existsById(UUID id);
}
