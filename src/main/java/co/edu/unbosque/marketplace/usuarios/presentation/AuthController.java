package co.edu.unbosque.marketplace.usuarios.presentation;

import co.edu.unbosque.marketplace.seguridad.UsuarioAutenticado;
import co.edu.unbosque.marketplace.usuarios.application.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var r = authService.login(request.correo(), request.contrasena());
        return new LoginResponse(r.token().token(), "Bearer", r.token().expiraEn(), UsuarioDto.desde(r.usuario()));
    }

    /** El frontend lo usa al recargar la pagina para recuperar usuario y rol. */
    @GetMapping("/me")
    public UsuarioDto me(@AuthenticationPrincipal UsuarioAutenticado actual) {
        return UsuarioDto.desde(authService.perfil(actual.id()));
    }
}
