package co.edu.unbosque.marketplace.usuarios.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recuperacion_contrasena")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @Column(name = "token_hash", nullable = false, updatable = false, length = 64)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false, updatable = false)
    private Instant expiraEn;

    @Column(name = "usado_en")
    private Instant usadoEn;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private Instant fechaCreacion;

    public TokenRecuperacion(UUID usuarioId, String tokenHash, Instant expiraEn) {
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;
    }
}
