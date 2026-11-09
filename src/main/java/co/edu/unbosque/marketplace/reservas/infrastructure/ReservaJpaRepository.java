package co.edu.unbosque.marketplace.reservas.infrastructure;

import co.edu.unbosque.marketplace.reservas.domain.EstadoReserva;
import co.edu.unbosque.marketplace.reservas.domain.Reserva;
import co.edu.unbosque.marketplace.reservas.domain.ReservaRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ReservaJpaRepository extends JpaRepository<Reserva, UUID>, ReservaRepository {

    @Override
    @Query("""
            select r from Reserva r
            where r.publicacionId = :publicacionId
              and r.estado in :estados
              and r.fechaLlegada <= :fin
              and r.fechaSalida > :inicio
            order by r.fechaLlegada
            """)
    List<Reserva> buscarSolapadas(@Param("publicacionId") UUID publicacionId,
                                  @Param("inicio") LocalDate inicio,
                                  @Param("fin") LocalDate finInclusivo,
                                  @Param("estados") Collection<EstadoReserva> estados);
}
