package co.edu.unbosque.marketplace.publicaciones.application;

import co.edu.unbosque.marketplace.publicaciones.domain.EstadoPublicacion;
import java.math.BigDecimal;
import java.util.UUID;

public record PublicacionResumen(UUID id, String titulo, String ciudad, EstadoPublicacion estado,
                                 int capacidad, BigDecimal precioNoche) {}
