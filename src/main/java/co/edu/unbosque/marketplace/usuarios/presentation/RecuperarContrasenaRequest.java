package co.edu.unbosque.marketplace.usuarios.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecuperarContrasenaRequest(
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido")
        @Size(max = 160) String correo) {
}
