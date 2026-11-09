package co.edu.unbosque.marketplace.usuarios.infrastructure;

import co.edu.unbosque.marketplace.usuarios.domain.TokenRecuperacion;
import co.edu.unbosque.marketplace.usuarios.domain.TokenRecuperacionRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TokenRecuperacionJpaRepository
        extends JpaRepository<TokenRecuperacion, UUID>, TokenRecuperacionRepository {

    @Override
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update TokenRecuperacion t set t.usadoEn = :ahora
            where t.usuarioId = :usuarioId and t.usadoEn is null
            """)
    int invalidarPendientes(@Param("usuarioId") UUID usuarioId, @Param("ahora") Instant ahora);

    @Override
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update TokenRecuperacion t set t.usadoEn = :ahora
            where t.id = :id and t.usadoEn is null and t.expiraEn > :ahora
            """)
    int consumir(@Param("id") UUID id, @Param("ahora") Instant ahora);
}
