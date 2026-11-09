package co.edu.unbosque.marketplace.publicaciones.infrastructure;

import co.edu.unbosque.marketplace.publicaciones.domain.Servicio;
import co.edu.unbosque.marketplace.publicaciones.domain.ServicioRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ServicioJpaRepository extends JpaRepository<Servicio, UUID>, ServicioRepository {
}
