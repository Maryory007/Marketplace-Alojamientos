package co.edu.unbosque.marketplace.seguridad;

import co.edu.unbosque.marketplace.compartido.CorrelationId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** ADR-011: crea el identificador de correlacion en el punto de entrada. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String id = UUID.randomUUID().toString();
        String entrante = req.getHeader(CorrelationId.HEADER);
        if (entrante != null) {
            try {
                id = UUID.fromString(entrante).toString();
            } catch (IllegalArgumentException ignorado) {
                // se usa el generado
            }
        }
        MDC.put(CorrelationId.CLAVE_MDC, id);
        res.setHeader(CorrelationId.HEADER, id);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(CorrelationId.CLAVE_MDC);
        }
    }
}
