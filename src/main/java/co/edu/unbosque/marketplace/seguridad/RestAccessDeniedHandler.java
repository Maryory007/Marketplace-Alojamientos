package co.edu.unbosque.marketplace.seguridad;

import co.edu.unbosque.marketplace.compartido.error.ErrorResponseWriter;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/** 403 + evento ACCESO_DENEGADO en auditoria. */
@Slf4j
@Component
class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorResponseWriter writer;
    private final AuditoriaPort auditoria;

    RestAccessDeniedHandler(ErrorResponseWriter writer, AuditoriaPort auditoria) {
        this.writer = writer;
        this.auditoria = auditoria;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
            throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var usuario = auth != null && auth.getPrincipal() instanceof UsuarioAutenticado u ? u : null;
        try {
            auditoria.registrar(new EventoAuditoria(
                    usuario != null ? usuario.id() : null,
                    "SEGURIDAD", "ACCESO_DENEGADO", "ENDPOINT", null,
                    EventoAuditoria.DENEGADO,
                    req.getMethod() + " " + req.getRequestURI()
                            + (usuario != null ? " - rol " + usuario.rol() : "")));
        } catch (RuntimeException e) {
            log.error("No se pudo auditar ACCESO_DENEGADO", e);
        }
        writer.escribir(res, 403, "ACCESO_DENEGADO", "No tienes permiso para realizar esta acción");
    }
}
