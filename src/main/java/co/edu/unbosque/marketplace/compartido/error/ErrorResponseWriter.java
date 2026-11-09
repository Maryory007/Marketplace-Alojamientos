package co.edu.unbosque.marketplace.compartido.error;

import co.edu.unbosque.marketplace.compartido.CorrelationId;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Escribe errores que ocurren en filtros, antes de llegar a MVC. */
@Component
public class ErrorResponseWriter {

    private final ObjectMapper mapper;

    public ErrorResponseWriter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void escribir(HttpServletResponse res, int estado, String codigo, String mensaje) throws IOException {
        res.setStatus(estado);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(mapper.writeValueAsString(
                new ApiError(codigo, mensaje, null, CorrelationId.actual())));
    }
}
