package co.edu.unbosque.marketplace.usuarios.presentation;

import java.time.Instant;

public record LoginResponse(String token, String tipo, Instant expiraEn, UsuarioDto usuario) {}
