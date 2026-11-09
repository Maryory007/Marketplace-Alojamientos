package co.edu.unbosque.marketplace.publicaciones.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeriodoDisponibleRepository {

    List<PeriodoDisponible> findByPublicacionIdOrderByFechaInicio(UUID publicacionId);

    Optional<PeriodoDisponible> findById(UUID id);

    /** true si algun periodo de la publicacion se cruza con [inicio, fin] (ambos inclusivos). */
    boolean existeCruce(UUID publicacionId, LocalDate inicio, LocalDate fin);

    /** Igual que existeCruce, ignorando el periodo que se esta editando. */
    boolean existeCruceExcluyendo(UUID publicacionId, LocalDate inicio, LocalDate fin, UUID excluirId);

    <S extends PeriodoDisponible> S saveAndFlush(S periodo);

    void delete(PeriodoDisponible periodo);

    void flush();
}
