package co.edu.unbosque.marketplace.publicaciones.presentation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ServicioAdicionalRequest(
        @NotBlank(message = "El nombre del servicio adicional es obligatorio")
        @Size(max = 80, message = "Máximo 80 caracteres") String nombre,
        @NotNull(message = "El costo es obligatorio")
        @DecimalMin(value = "0", message = "El costo no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Costo inválido") BigDecimal costo) {
}
