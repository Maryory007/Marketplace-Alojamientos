package co.edu.unbosque.marketplace.compartido.error;

import co.edu.unbosque.marketplace.compartido.CorrelationId;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExcepcionNegocio.class)
    ResponseEntity<ApiError> negocio(ExcepcionNegocio ex) {
        return ResponseEntity.status(ex.getTipo().getEstadoHttp())
                .body(new ApiError(ex.getCodigo(), ex.getMessage(), ex.getDetalles(), CorrelationId.actual()));
    }

    /** Se relanza para que lo resuelva RestAccessDeniedHandler (403 + auditoria ACCESO_DENEGADO). */
    @ExceptionHandler(AccessDeniedException.class)
    void accesoDenegado(AccessDeniedException ex) {
        throw ex;
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> inesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("ERROR_INTERNO", "Ocurrió un error inesperado", null, CorrelationId.actual()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ApiError.DetalleCampo> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiError.DetalleCampo(f.getField(), f.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(status).body(
                new ApiError("SOLICITUD_INVALIDA", "Hay campos inválidos en la solicitud", detalles,
                        CorrelationId.actual()));
    }

    /** Cualquier otra excepcion estandar de Spring MVC sale con el mismo formato. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String codigo = switch (status.value()) {
            case 400 -> "SOLICITUD_INVALIDA";
            case 404 -> "RECURSO_NO_ENCONTRADO";
            case 405 -> "METODO_NO_PERMITIDO";
            case 415 -> "TIPO_CONTENIDO_NO_SOPORTADO";
            default -> "ERROR_" + status.value();
        };
        return ResponseEntity.status(status).headers(headers).body(
                new ApiError(codigo, "No se pudo procesar la solicitud", null, CorrelationId.actual()));
    }
}
