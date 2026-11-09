package co.edu.unbosque.marketplace.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private static final String CLAIM_ROL = "rol";

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        byte[] bytes = props.secret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 bytes (HS256)");
        }
        this.props = props;
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenEmitido emitir(UUID usuarioId, String rol) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(props.expirationMinutes(), ChronoUnit.MINUTES);
        String token = Jwts.builder()
                .issuer(props.issuer())
                .subject(usuarioId.toString())
                .claim(CLAIM_ROL, rol)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(key)
                .compact();
        return new TokenEmitido(token, expira);
    }

    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(props.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String rol = claims.get(CLAIM_ROL, String.class);
            if (rol == null) {
                return Optional.empty();
            }
            return Optional.of(new UsuarioAutenticado(UUID.fromString(claims.getSubject()), rol));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
