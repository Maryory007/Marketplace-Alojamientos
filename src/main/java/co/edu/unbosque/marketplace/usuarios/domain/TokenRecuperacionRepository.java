package co.edu.unbosque.marketplace.usuarios.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TokenRecuperacionRepository {

    <S extends TokenRecuperacion> S save(S token);

    Optional<TokenRecuperacion> findByTokenHash(String tokenHash);

    /** Invalida los enlaces anteriores aun sin usar del usuario. */
    int invalidarPendientes(UUID usuarioId, Instant ahora);

    /** Marca el enlace como usado solo si sigue vigente y sin usar (atomico). Devuelve 1 si lo logro. */
    int consumir(UUID id, Instant ahora);
}
