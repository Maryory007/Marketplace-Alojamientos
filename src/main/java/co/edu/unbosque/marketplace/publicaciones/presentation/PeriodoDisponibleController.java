package co.edu.unbosque.marketplace.publicaciones.presentation;

import co.edu.unbosque.marketplace.publicaciones.application.PeriodoDisponibleService;
import co.edu.unbosque.marketplace.seguridad.UsuarioAutenticado;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Solo ANFITRION (SecurityConfig: /api/anfitrion/**); la propiedad se verifica en el servicio. */
@RestController
@RequestMapping("/api/anfitrion/publicaciones/{publicacionId}/periodos")
public class PeriodoDisponibleController {

    private final PeriodoDisponibleService service;

    public PeriodoDisponibleController(PeriodoDisponibleService service) {
        this.service = service;
    }

    @GetMapping
    public List<PeriodoDto> listar(@PathVariable UUID publicacionId,
                                   @AuthenticationPrincipal UsuarioAutenticado actual) {
        return service.listar(publicacionId, actual).stream().map(PeriodoDto::desde).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeriodoDto registrar(@PathVariable UUID publicacionId, @Valid @RequestBody PeriodoRequest req,
                                @AuthenticationPrincipal UsuarioAutenticado actual) {
        return PeriodoDto.desde(service.registrar(publicacionId, req.fechaInicio(), req.fechaFin(), actual));
    }

    @PutMapping("/{periodoId}")
    public PeriodoDto editar(@PathVariable UUID publicacionId, @PathVariable UUID periodoId,
                             @Valid @RequestBody PeriodoRequest req,
                             @AuthenticationPrincipal UsuarioAutenticado actual) {
        return PeriodoDto.desde(service.editar(publicacionId, periodoId, req.fechaInicio(), req.fechaFin(), actual));
    }

    @DeleteMapping("/{periodoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID publicacionId, @PathVariable UUID periodoId,
                         @AuthenticationPrincipal UsuarioAutenticado actual) {
        service.eliminar(publicacionId, periodoId, actual);
    }
}
