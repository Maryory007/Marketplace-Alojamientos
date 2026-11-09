package co.edu.unbosque.marketplace.publicaciones.application;

import co.edu.unbosque.marketplace.publicaciones.domain.EstadoPublicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.ModoReserva;
import co.edu.unbosque.marketplace.publicaciones.domain.TipoAlojamiento;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PublicacionDetalle(
        UUID id,
        UUID anfitrionId,
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
        EstadoPublicacion estado,
        String motivoEstado,
        List<UUID> serviciosIds,
        List<ServicioAdicionalVista> serviciosAdicionales,
        List<String> imagenes) {

    public record ServicioAdicionalVista(String nombre, BigDecimal costo) {}
}
