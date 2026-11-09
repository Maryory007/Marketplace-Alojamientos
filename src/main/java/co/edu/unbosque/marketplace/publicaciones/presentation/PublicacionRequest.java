package co.edu.unbosque.marketplace.publicaciones.presentation;

import co.edu.unbosque.marketplace.publicaciones.domain.DatosPublicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.ModoReserva;
import co.edu.unbosque.marketplace.publicaciones.domain.ServicioAdicionalPublicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.TipoAlojamiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cuerpo de crear/editar. Servicios adicionales e imagenes se reemplazan completos en cada edicion;
 * el orden de las imagenes es el de la lista (la primera es la portada).
 */
public record PublicacionRequest(
        @NotNull(message = "La política de cancelación es obligatoria") UUID politicaCancelacionId,
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título admite máximo 150 caracteres") String titulo,
        @NotBlank(message = "La descripción es obligatoria") String descripcion,
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 80, message = "La ciudad admite máximo 80 caracteres") String ciudad,
        @NotBlank(message = "La ubicación es obligatoria")
        @Size(max = 255, message = "La ubicación admite máximo 255 caracteres") String ubicacionTextual,
        @NotNull(message = "El tipo de alojamiento es obligatorio") TipoAlojamiento tipo,
        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser mayor que 0") Integer capacidad,
        @NotNull(message = "El precio por noche es obligatorio")
        @DecimalMin(value = "0", inclusive = false, message = "El precio por noche debe ser mayor que 0")
        @Digits(integer = 10, fraction = 2, message = "Precio inválido") BigDecimal precioNoche,
        @DecimalMin(value = "0", message = "La tarifa de limpieza no puede ser negativa")
        @Digits(integer = 10, fraction = 2, message = "Tarifa inválida") BigDecimal tarifaLimpieza,
        @Size(max = 4000, message = "Las reglas admiten máximo 4000 caracteres") String reglas,
        @NotNull(message = "El modo de reserva es obligatorio") ModoReserva modoReserva,
        Set<UUID> serviciosIds,
        @Size(max = 20, message = "Máximo 20 servicios adicionales")
        List<@Valid @NotNull ServicioAdicionalRequest> serviciosAdicionales,
        @Size(max = 15, message = "Máximo 15 imágenes")
        List<@NotBlank(message = "La ruta de la imagen no puede estar vacía")
             @Size(max = 500, message = "La ruta admite máximo 500 caracteres") String> imagenes) {

    public DatosPublicacion aDatos() {
        return new DatosPublicacion(
                politicaCancelacionId,
                titulo.trim(),
                descripcion.trim(),
                ciudad.trim(),
                ubicacionTextual.trim(),
                tipo,
                capacidad,
                precioNoche,
                tarifaLimpieza == null ? BigDecimal.ZERO : tarifaLimpieza,
                reglas == null ? "" : reglas.trim(),
                modoReserva,
                serviciosIds == null ? Set.of() : Set.copyOf(serviciosIds),
                serviciosAdicionales == null ? List.of() : serviciosAdicionales.stream()
                        .map(s -> new ServicioAdicionalPublicacion(s.nombre().trim(), s.costo())).toList(),
                imagenes == null ? List.of() : imagenes.stream().map(String::trim).toList());
    }
}
