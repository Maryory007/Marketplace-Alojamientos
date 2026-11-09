package co.edu.unbosque.marketplace.seguridad;

import java.util.UUID;

/** Principal que usan todos los modulos (se obtiene con @AuthenticationPrincipal). */
public record UsuarioAutenticado(UUID id, String rol) {}
