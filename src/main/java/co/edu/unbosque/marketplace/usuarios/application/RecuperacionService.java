package co.edu.unbosque.marketplace.usuarios.application;

import co.edu.unbosque.marketplace.compartido.error.ExcepcionNegocio;
import co.edu.unbosque.marketplace.compartido.error.TipoError;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import co.edu.unbosque.marketplace.usuarios.domain.EstadoCuenta;
import co.edu.unbosque.marketplace.usuarios.domain.TokenRecuperacion;
import co.edu.unbosque.marketplace.usuarios.domain.TokenRecuperacionRepository;
import co.edu.unbosque.marketplace.usuarios.domain.Usuario;
import co.edu.unbosque.marketplace.usuarios.domain.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecuperacionService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacionService.class);
    private static final String MODULO = "USUARIOS";
    private static final String MENSAJE =
            "Si el correo está registrado, se generó un enlace para restablecer la contraseña";

    private final UsuarioRepository usuarios;
    private final TokenRecuperacionRepository tokens;
    private final PasswordEncoder encoder;
    private final AuditoriaPort auditoria;
    private final String urlBase;
    private final long expiracionMinutos;
    private final boolean exponerEnlace;
    private final SecureRandom random = new SecureRandom();

    public RecuperacionService(UsuarioRepository usuarios, TokenRecuperacionRepository tokens,
                               PasswordEncoder encoder, AuditoriaPort auditoria,
                               @Value("${app.recuperacion.url-base}") String urlBase,
                               @Value("${app.recuperacion.expiracion-minutos}") long expiracionMinutos,
                               @Value("${app.recuperacion.exponer-enlace}") boolean exponerEnlace) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
        this.auditoria = auditoria;
        this.urlBase = urlBase;
        this.expiracionMinutos = expiracionMinutos;
        this.exponerEnlace = exponerEnlace;
    }

    /** Criterio 1: con correo registrado queda registrado el enlace (simulado, no se envia correo). */
    @Transactional
    public RecuperacionResponse solicitar(String correo) {
        String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
        Usuario u = usuarios.findByCorreoIgnoreCase(correoNormalizado).orElse(null);
        if (u == null || u.getEstadoCuenta() == EstadoCuenta.DESACTIVADA) {
            return new RecuperacionResponse(MENSAJE, null); // misma respuesta: no revela si existe
        }

        Instant ahora = Instant.now();
        tokens.invalidarPendientes(u.getId(), ahora);

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.save(new TokenRecuperacion(u.getId(), hash(token), ahora.plus(expiracionMinutos, ChronoUnit.MINUTES)));

        String enlace = urlBase + "?token=" + token;
        log.info("[CORREO SIMULADO] Enlace de recuperacion para {} (vigente {} min): {}",
                u.getCorreo(), expiracionMinutos, enlace);
        auditoria.registrar(new EventoAuditoria(u.getId(), MODULO, "RECUPERACION_SOLICITADA", "USUARIO",
                u.getId().toString(), EventoAuditoria.EXITO, "Enlace simulado generado"));
        return new RecuperacionResponse(MENSAJE, exponerEnlace ? enlace : null);
    }

    /** Criterio 2: con enlace vigente cambia la contrasena y el enlace deja de servir. */
    @Transactional
    public void restablecer(String token, String nuevaContrasena) {
        TokenRecuperacion t = tokens.findByTokenHash(hash(token.trim())).orElseThrow(this::enlaceInvalido);
        UUID usuarioId = t.getUsuarioId();
        UUID tokenId = t.getId();
        if (tokens.consumir(tokenId, Instant.now()) != 1) { // vencido o ya usado
            throw enlaceInvalido();
        }
        Usuario u = usuarios.findById(usuarioId).orElseThrow(this::enlaceInvalido);
        u.cambiarContrasena(encoder.encode(nuevaContrasena));
        usuarios.save(u);
        auditoria.registrar(new EventoAuditoria(usuarioId, MODULO, "CONTRASENA_RESTABLECIDA", "USUARIO",
                usuarioId.toString(), EventoAuditoria.EXITO, null));
    }

    private ExcepcionNegocio enlaceInvalido() {
        return new ExcepcionNegocio(TipoError.SOLICITUD_INVALIDA, "ENLACE_INVALIDO",
                "El enlace de restablecimiento no es válido o ya venció");
    }

    private static String hash(String token) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
