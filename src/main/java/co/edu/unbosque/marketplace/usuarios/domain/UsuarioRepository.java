package co.edu.unbosque.marketplace.usuarios.domain;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    Optional<Usuario> findById(UUID id);
    <S extends Usuario> S save(S usuario);
}
