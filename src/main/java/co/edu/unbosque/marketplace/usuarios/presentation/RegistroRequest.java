package co.edu.unbosque.marketplace.usuarios.presentation;

import co.edu.unbosque.marketplace.usuarios.domain.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 120) String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido")
        @Size(max = 160) String correo,
        @Size(max = 30) String telefono,
        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(regexp = PoliticaContrasena.REGEX, message = PoliticaContrasena.MENSAJE) String contrasena,
        @NotNull(message = "El rol es obligatorio")
        @Schema(allowableValues = {"HUESPED", "ANFITRION"}) Rol rol) {
}
