package co.edu.unbosque.marketplace.usuarios.application;

import co.edu.unbosque.marketplace.compartido.error.ExcepcionNegocio;
import co.edu.unbosque.marketplace.compartido.error.TipoError;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import co.edu.unbosque.marketplace.usuarios.domain.Rol;
import co.edu.unbosque.marketplace.usuarios.domain.Usuario;
import co.edu.unbosque.marketplace.usuarios.domain.UsuarioRepository;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * No es @Transactional a proposito: el usuario debe quedar confirmado antes de auditar,
 * porque la auditoria usa su propia transaccion y referencia al usuario por clave foranea.
 */
@Service
public class RegistroService {

    private static final String MODULO = "USUARIOS";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final AuditoriaPort auditoria;

    public RegistroService(UsuarioRepository usuarios, PasswordEncoder encoder, AuditoriaPort auditoria) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.auditoria = auditoria;
    }

    public Usuario registrar(String nombre, String correo, String telefono, String contrasena, Rol rol) {
        if (rol == Rol.ADMINISTRADOR) {
            throw new ExcepcionNegocio(TipoError.SOLICITUD_INVALIDA, "ROL_NO_PERMITIDO",
                    "Solo puedes registrarte como HUESPED o ANFITRION");
        }
        String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
        if (usuarios.findByCorreoIgnoreCase(correoNormalizado).isPresent()) {
            throw correoDuplicado(correoNormalizado);
        }

        Usuario nuevo = new Usuario(nombre.trim(), correoNormalizado,
                telefono == null || telefono.isBlank() ? null : telefono.trim(),
                encoder.encode(contrasena), rol);
        Usuario guardado;
        try {
            guardado = usuarios.save(nuevo);
        } catch (DataIntegrityViolationException e) { // registro simultaneo con el mismo correo
            throw correoDuplicado(correoNormalizado);
        }
        auditoria.registrar(new EventoAuditoria(guardado.getId(), MODULO, "REGISTRO_EXITOSO", "USUARIO",
                guardado.getId().toString(), EventoAuditoria.EXITO, "Rol " + rol));
        return guardado;
    }

    private ExcepcionNegocio correoDuplicado(String correo) {
        auditoria.registrar(new EventoAuditoria(null, MODULO, "REGISTRO_RECHAZADO", "USUARIO", null,
                EventoAuditoria.FALLO, "Correo ya registrado"));
        return new ExcepcionNegocio(TipoError.CONFLICTO, "CORREO_YA_REGISTRADO",
                "El correo ya está registrado");
    }
}
