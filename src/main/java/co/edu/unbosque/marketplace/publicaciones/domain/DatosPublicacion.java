package co.edu.unbosque.marketplace.publicaciones.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Datos editables de una publicacion (los usa tanto la creacion como la edicion). */
public record DatosPublicacion(
        UUID politicaCancelacionId,
        String titulo,
        String descripcion,
        String ciudad,
        String ubicacionTextual,
        TipoAlojamiento tipo,
        int capacidad,
        BigDecimal precioNoche,
        BigDecimal tarifaLimpieza,
        String reglas,
        ModoReserva modoReserva,
        Set<UUID> serviciosIds,
        List<ServicioAdicionalPublicacion> serviciosAdicionales,
        List<String> imagenes) {
}
