package co.edu.unbosque.marketplace.usuarios.infrastructure;

import co.edu.unbosque.marketplace.usuarios.domain.Usuario;
import co.edu.unbosque.marketplace.usuarios.domain.UsuarioRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID>, UsuarioRepository {
}
