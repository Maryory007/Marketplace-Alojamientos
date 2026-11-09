package co.edu.unbosque.marketplace.usuarios.presentation;

import co.edu.unbosque.marketplace.usuarios.domain.Usuario;
import java.util.UUID;

public record UsuarioDto(UUID id, String nombre, String correo, String rol) {
    static UsuarioDto desde(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getCorreo(), u.getRol().name());
    }
}
