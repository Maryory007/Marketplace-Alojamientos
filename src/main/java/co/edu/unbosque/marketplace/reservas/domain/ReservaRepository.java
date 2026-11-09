package co.edu.unbosque.marketplace.reservas.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReservaRepository {
    /** Reservas cuyas noches [llegada, salida) se cruzan con el rango [inicio, finInclusivo]. */
    List<Reserva> buscarSolapadas(UUID publicacionId, LocalDate inicio, LocalDate finInclusivo,
                                  Collection<EstadoReserva> estados);
}
