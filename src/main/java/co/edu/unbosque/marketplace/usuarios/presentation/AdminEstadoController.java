package co.edu.unbosque.marketplace.usuarios.presentation;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Temporal: permite probar el criterio 2 (403 + ACCESO_DENEGADO) hasta que exista un endpoint real de admin. */
@RestController
@RequestMapping("/api/admin")
class AdminEstadoController {

    @GetMapping("/estado")
    Map<String, String> estado() {
        return Map.of("estado", "ok");
    }
}
