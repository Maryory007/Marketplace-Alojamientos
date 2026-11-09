package co.edu.unbosque.marketplace.usuarios.application;

import co.edu.unbosque.marketplace.compartido.error.ExcepcionNegocio;
import co.edu.unbosque.marketplace.compartido.error.TipoError;
import co.edu.unbosque.marketplace.seguridad.JwtService;
import co.edu.unbosque.marketplace.seguridad.TokenEmitido;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import co.edu.unbosque.marketplace.usuarios.domain.EstadoCuenta;
import co.edu.unbosque.marketplace.usuarios.domain.Usuario;
import co.edu.unbosque.marketplace.usuarios.domain.UsuarioRepository;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String MODULO = "USUARIOS";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuditoriaPort auditoria;
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder,
                       JwtService jwtService, AuditoriaPort auditoria) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.auditoria = auditoria;
        this.hashFicticio = encoder.encode("hash-ficticio"); // iguala tiempos si el correo no existe
    }

    public record ResultadoLogin(TokenEmitido token, Usuario usuario) {}

    public ResultadoLogin login(String correo, String contrasena) {
        String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
        Usuario u = usuarios.findByCorreoIgnoreCase(correoNormalizado).orElse(null);

        if (u == null) {
            encoder.matches(contrasena, hashFicticio);
            fallo(null, "Correo no registrado");
            throw credencialesInvalidas();
        }
        if (!encoder.matches(contrasena, u.getContrasenaHash())) {
            fallo(u, "Contraseña incorrecta");
            throw credencialesInvalidas();
        }
        if (u.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            fallo(u, "Cuenta " + u.getEstadoCuenta());
            throw cuentaNoActiva(u);
        }

        TokenEmitido token = jwtService.emitir(u.getId(), u.getRol().name());
        auditoria.registrar(new EventoAuditoria(u.getId(), MODULO, "LOGIN_EXITOSO", "USUARIO",
                u.getId().toString(), EventoAuditoria.EXITO, null));
        return new ResultadoLogin(token, u);
    }

    public Usuario perfil(UUID usuarioId) {
        Usuario u = usuarios.findById(usuarioId).orElseThrow(() ->
                new ExcepcionNegocio(TipoError.NO_AUTENTICADO, "USUARIO_NO_ENCONTRADO",
                        "La sesión ya no es válida"));
        if (u.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw cuentaNoActiva(u);
        }
        return u;
    }

    private void fallo(Usuario u, String motivo) {
        auditoria.registrar(new EventoAuditoria(u != null ? u.getId() : null, MODULO, "LOGIN_FALLIDO",
                "USUARIO", u != null ? u.getId().toString() : null, EventoAuditoria.FALLO, motivo));
    }

    private ExcepcionNegocio credencialesInvalidas() {
        return new ExcepcionNegocio(TipoError.NO_AUTENTICADO, "CREDENCIALES_INVALIDAS",
                "Correo o contraseña incorrectos");
    }

    private ExcepcionNegocio cuentaNoActiva(Usuario u) {
        return u.getEstadoCuenta() == EstadoCuenta.RESTRINGIDA
                ? new ExcepcionNegocio(TipoError.PROHIBIDO, "CUENTA_RESTRINGIDA", "Tu cuenta está restringida")
                : new ExcepcionNegocio(TipoError.PROHIBIDO, "CUENTA_DESACTIVADA", "Tu cuenta está desactivada");
    }
}
