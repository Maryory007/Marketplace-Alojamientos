package co.edu.unbosque.marketplace.publicaciones.application;

import co.edu.unbosque.marketplace.compartido.error.ExcepcionNegocio;
import co.edu.unbosque.marketplace.compartido.error.TipoError;
import co.edu.unbosque.marketplace.publicaciones.domain.PeriodoDisponible;
import co.edu.unbosque.marketplace.publicaciones.domain.PeriodoDisponibleRepository;
import co.edu.unbosque.marketplace.publicaciones.domain.Publicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.PublicacionRepository;
import co.edu.unbosque.marketplace.reservas.ReservaFacade;
import co.edu.unbosque.marketplace.reservas.ReservaResumen;
import co.edu.unbosque.marketplace.seguridad.UsuarioAutenticado;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeriodoDisponibleService {

    private static final String MODULO = "PUBLICACIONES";
    private static final String ENTIDAD = "PERIODO_DISPONIBLE";
    private static final String RESTRICCION_CRUCE = "ex_periodo_sin_cruce";
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final PublicacionRepository publicaciones;
    private final PeriodoDisponibleRepository periodos;
    private final ReservaFacade reservas;
    private final AuditoriaPort auditoria;

    public PeriodoDisponibleService(PublicacionRepository publicaciones, PeriodoDisponibleRepository periodos,
                                    ReservaFacade reservas, AuditoriaPort auditoria) {
        this.publicaciones = publicaciones;
        this.periodos = periodos;
        this.reservas = reservas;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<PeriodoDisponible> listar(UUID publicacionId, UsuarioAutenticado actual) {
        verificarPropiedad(publicacionId, actual);
        return periodos.findByPublicacionIdOrderByFechaInicio(publicacionId);
    }

    /** Criterios 1 y 2: sin cruce queda disponible; con cruce responde 409. */
    @Transactional
    public PeriodoDisponible registrar(UUID publicacionId, LocalDate inicio, LocalDate fin,
                                       UsuarioAutenticado actual) {
        verificarPropiedad(publicacionId, actual);
        validarFechas(inicio, fin);
        if (periodos.existeCruce(publicacionId, inicio, fin)) {
            throw cruce();
        }
        PeriodoDisponible nuevo = guardar(new PeriodoDisponible(publicacionId, inicio, fin));
        auditar(actual, "PERIODO_CREADO", nuevo.getId(), rango(inicio, fin));
        return nuevo;
    }

    @Transactional
    public PeriodoDisponible editar(UUID publicacionId, UUID periodoId, LocalDate inicio, LocalDate fin,
                                    UsuarioAutenticado actual) {
        verificarPropiedad(publicacionId, actual);
        validarFechas(inicio, fin);
        PeriodoDisponible periodo = buscar(publicacionId, periodoId);

        // No se puede encoger un periodo dejando reservas fuera de el.
        LocalDate ultimaSalida = fin.plusDays(1); // la salida puede ser el dia siguiente a la ultima noche
        List<ReservaResumen> afectadas = reservas
                .reservasActivasEnRango(publicacionId, periodo.getFechaInicio(), periodo.getFechaFin()).stream()
                .filter(r -> r.fechaLlegada().isBefore(inicio) || r.fechaSalida().isAfter(ultimaSalida))
                .toList();
        if (!afectadas.isEmpty()) {
            throw conReservas(afectadas);
        }
        if (periodos.existeCruceExcluyendo(publicacionId, inicio, fin, periodoId)) {
            throw cruce();
        }
        periodo.actualizar(inicio, fin);
        guardar(periodo);
        auditar(actual, "PERIODO_EDITADO", periodoId, rango(inicio, fin));
        return periodo;
    }

    /** Criterio 3: con reservas activas en el periodo no se elimina y se listan las afectadas. */
    @Transactional
    public void eliminar(UUID publicacionId, UUID periodoId, UsuarioAutenticado actual) {
        verificarPropiedad(publicacionId, actual);
        PeriodoDisponible periodo = buscar(publicacionId, periodoId);

        List<ReservaResumen> afectadas = reservas.reservasActivasEnRango(
                publicacionId, periodo.getFechaInicio(), periodo.getFechaFin());
        if (!afectadas.isEmpty()) {
            throw conReservas(afectadas);
        }
        periodos.delete(periodo);
        periodos.flush();
        auditar(actual, "PERIODO_ELIMINADO", periodoId, rango(periodo.getFechaInicio(), periodo.getFechaFin()));
    }

    // ---------- helpers ----------

    private Publicacion verificarPropiedad(UUID publicacionId, UsuarioAutenticado actual) {
        Publicacion pub = publicaciones.findById(publicacionId).orElseThrow(() ->
                new ExcepcionNegocio(TipoError.NO_ENCONTRADO, "PUBLICACION_NO_ENCONTRADA",
                        "La publicación no existe"));
        if (!pub.getAnfitrionId().equals(actual.id())) {
            auditoria.registrar(new EventoAuditoria(actual.id(), MODULO, "ACCESO_DENEGADO", "PUBLICACION",
                    publicacionId.toString(), EventoAuditoria.DENEGADO,
                    "El usuario no es dueño de la publicación"));
            throw new ExcepcionNegocio(TipoError.PROHIBIDO, "ACCESO_DENEGADO",
                    "No tienes permiso para gestionar esta publicación");
        }
        return pub;
    }

    private PeriodoDisponible buscar(UUID publicacionId, UUID periodoId) {
        return periodos.findById(periodoId)
                .filter(p -> p.getPublicacionId().equals(publicacionId))
                .orElseThrow(() -> new ExcepcionNegocio(TipoError.NO_ENCONTRADO, "PERIODO_NO_ENCONTRADO",
                        "El periodo no existe"));
    }

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (fin.isBefore(inicio)) {
            throw new ExcepcionNegocio(TipoError.SOLICITUD_INVALIDA, "FECHAS_INVALIDAS",
                    "La fecha de fin no puede ser anterior a la de inicio");
        }
        if (inicio.isBefore(LocalDate.now(ZONA))) {
            throw new ExcepcionNegocio(TipoError.REGLA_NEGOCIO, "PERIODO_EN_EL_PASADO",
                    "El periodo no puede comenzar en el pasado");
        }
    }

    /** La restriccion de exclusion de la base es la defensa final ante solicitudes concurrentes. */
    private PeriodoDisponible guardar(PeriodoDisponible periodo) {
        try {
            return periodos.saveAndFlush(periodo);
        } catch (DataIntegrityViolationException e) {
            String causa = e.getMostSpecificCause().getMessage();
            if (causa != null && causa.contains(RESTRICCION_CRUCE)) {
                throw cruce();
            }
            throw e;
        }
    }

    private ExcepcionNegocio cruce() {
        return new ExcepcionNegocio(TipoError.CONFLICTO, "PERIODO_CRUZADO",
                "El periodo se cruza con otro periodo ya registrado");
    }

    private ExcepcionNegocio conReservas(List<ReservaResumen> afectadas) {
        return new ExcepcionNegocio(TipoError.CONFLICTO, "PERIODO_CON_RESERVAS",
                "El periodo tiene reservas activas y no se puede modificar ni eliminar", afectadas);
    }

    private void auditar(UsuarioAutenticado actual, String accion, UUID periodoId, String motivo) {
        auditoria.registrar(new EventoAuditoria(actual.id(), MODULO, accion, ENTIDAD,
                periodoId.toString(), EventoAuditoria.EXITO, motivo));
    }

    private String rango(LocalDate inicio, LocalDate fin) {
        return inicio + " a " + fin;
    }
}
