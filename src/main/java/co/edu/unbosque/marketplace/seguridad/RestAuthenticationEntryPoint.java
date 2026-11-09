package co.edu.unbosque.marketplace.seguridad;

import co.edu.unbosque.marketplace.compartido.error.ErrorResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ErrorResponseWriter writer;

    RestAuthenticationEntryPoint(ErrorResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
            throws IOException {
        writer.escribir(res, 401, "NO_AUTENTICADO", "Debes iniciar sesión para acceder a este recurso");
    }
}
