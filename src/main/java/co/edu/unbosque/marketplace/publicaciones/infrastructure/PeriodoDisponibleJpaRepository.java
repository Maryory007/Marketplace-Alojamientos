package co.edu.unbosque.marketplace.publicaciones.infrastructure;

import co.edu.unbosque.marketplace.publicaciones.domain.PeriodoDisponible;
import co.edu.unbosque.marketplace.publicaciones.domain.PeriodoDisponibleRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface PeriodoDisponibleJpaRepository
        extends JpaRepository<PeriodoDisponible, UUID>, PeriodoDisponibleRepository {

    @Override
    @Query("""
            select count(p) > 0 from PeriodoDisponible p
            where p.publicacionId = :publicacionId
              and p.fechaInicio <= :fin and p.fechaFin >= :inicio
            """)
    boolean existeCruce(@Param("publicacionId") UUID publicacionId,
                        @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Override
    @Query("""
            select count(p) > 0 from PeriodoDisponible p
            where p.publicacionId = :publicacionId
              and p.id <> :excluirId
              and p.fechaInicio <= :fin and p.fechaFin >= :inicio
            """)
    boolean existeCruceExcluyendo(@Param("publicacionId") UUID publicacionId,
                                  @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin,
                                  @Param("excluirId") UUID excluirId);
}
