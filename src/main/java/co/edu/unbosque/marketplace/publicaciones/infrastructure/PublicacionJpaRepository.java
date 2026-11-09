package co.edu.unbosque.marketplace.publicaciones.infrastructure;

import co.edu.unbosque.marketplace.publicaciones.domain.Publicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.PublicacionRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PublicacionJpaRepository extends JpaRepository<Publicacion, UUID>, PublicacionRepository {
}
