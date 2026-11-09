package co.edu.unbosque.marketplace.seguridad;

import java.time.Instant;

public record TokenEmitido(String token, Instant expiraEn) {}
