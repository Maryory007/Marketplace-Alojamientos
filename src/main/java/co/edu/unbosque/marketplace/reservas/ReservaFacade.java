package co.edu.unbosque.marketplace.reservas;

import co.edu.unbosque.marketplace.reservas.domain.EstadoReserva;
import co.edu.unbosque.marketplace.reservas.domain.ReservaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** API publica del modulo Reservas (ADR-001): los demas modulos solo usan esta clase. */
@Service
public class ReservaFacade {

    /** Estados que ocupan el periodo (ADR-009). CANCELADA y PAGO_FALLIDO lo liberan. */
    private static final List<EstadoReserva> ESTADOS_QUE_OCUPAN = List.of(
            EstadoReserva.PENDIENTE_PAGO, EstadoReserva.PENDIENTE_APROBACION,
            EstadoReserva.CONFIRMADA, EstadoReserva.EN_REVISION, EstadoReserva.COMPLETADA);

    private final ReservaRepository reservas;

    ReservaFacade(ReservaRepository reservas) {
        this.reservas = reservas;
    }

    /** Reservas que ocupan alguna noche del rango [inicio, finInclusivo]. */
    @Transactional(readOnly = true)
    public List<ReservaResumen> reservasActivasEnRango(UUID publicacionId, LocalDate inicio,
                                                       LocalDate finInclusivo) {
        return reservas.buscarSolapadas(publicacionId, inicio, finInclusivo, ESTADOS_QUE_OCUPAN).stream()
                .map(r -> new ReservaResumen(r.getId(), r.getFechaLlegada(), r.getFechaSalida(),
                        r.getNumeroHuespedes(), r.getEstado().name()))
                .toList();
    }
}
