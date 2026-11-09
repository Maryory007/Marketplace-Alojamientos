package co.edu.unbosque.marketplace.usuarios.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RestablecerContrasenaRequest(
        @NotBlank(message = "El token es obligatorio") @Size(max = 200) String token,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Pattern(regexp = PoliticaContrasena.REGEX, message = PoliticaContrasena.MENSAJE) String nuevaContrasena) {
}
