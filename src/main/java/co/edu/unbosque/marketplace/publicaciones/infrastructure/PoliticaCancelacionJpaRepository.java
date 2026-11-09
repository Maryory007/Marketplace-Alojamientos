package co.edu.unbosque.marketplace.publicaciones.infrastructure;

import co.edu.unbosque.marketplace.publicaciones.domain.PoliticaCancelacion;
import co.edu.unbosque.marketplace.publicaciones.domain.PoliticaCancelacionRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PoliticaCancelacionJpaRepository
        extends JpaRepository<PoliticaCancelacion, UUID>, PoliticaCancelacionRepository {
}
