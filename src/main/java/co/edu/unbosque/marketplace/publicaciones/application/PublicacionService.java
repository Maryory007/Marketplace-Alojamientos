package co.edu.unbosque.marketplace.publicaciones.application;

import co.edu.unbosque.marketplace.compartido.error.ApiError;
import co.edu.unbosque.marketplace.compartido.error.ExcepcionNegocio;
import co.edu.unbosque.marketplace.compartido.error.TipoError;
import co.edu.unbosque.marketplace.publicaciones.domain.DatosPublicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.EstadoPublicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.PoliticaCancelacion;
import co.edu.unbosque.marketplace.publicaciones.domain.PoliticaCancelacionRepository;
import co.edu.unbosque.marketplace.publicaciones.domain.Publicacion;
import co.edu.unbosque.marketplace.publicaciones.domain.PublicacionRepository;
import co.edu.unbosque.marketplace.publicaciones.domain.Servicio;
import co.edu.unbosque.marketplace.publicaciones.domain.ServicioRepository;
import co.edu.unbosque.marketplace.seguridad.UsuarioAutenticado;
import co.edu.unbosque.marketplace.trazabilidad.AuditoriaPort;
import co.edu.unbosque.marketplace.trazabilidad.EventoAuditoria;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicacionService {

    private static final String MODULO = "PUBLICACIONES";
    private static final String ENTIDAD = "PUBLICACION";
    /** Estados en los que el anfitrion puede modificar la publicacion. */
    private static final Set<EstadoPublicacion> EDITABLES = EnumSet.of(
            EstadoPublicacion.BORRADOR, EstadoPublicacion.ACTIVA, EstadoPublicacion.PAUSADA);

    private final PublicacionRepository publicaciones;
    private final PoliticaCancelacionRepository politicas;
    private final ServicioRepository servicios;
    private final AuditoriaPort auditoria;

    public PublicacionService(PublicacionRepository publicaciones, PoliticaCancelacionRepository politicas,
                              ServicioRepository servicios, AuditoriaPort auditoria) {
        this.publicaciones = publicaciones;
        this.politicas = politicas;
        this.servicios = servicios;
        this.auditoria = auditoria;
    }

    /** Criterio 1: con campos validos queda en BORRADOR. */
    @Transactional
    public PublicacionDetalle crear(DatosPublicacion datos, UsuarioAutenticado actual) {
        validarReferencias(datos);
        Publicacion p = new Publicacion(actual.id());
        p.aplicar(datos);
        p = publicaciones.save(p);
        auditar(actual, "PUBLICACION_CREADA", p.getId(), "Queda en " + p.getEstado());
        return detalle(p);
    }

    @Transactional
    public PublicacionDetalle editar(UUID id, DatosPublicacion datos, UsuarioAutenticado actual) {
        Publicacion p = propia(id, actual);
        if (!EDITABLES.contains(p.getEstado())) {
            throw estadoNoPermitido(p, "editar");
        }
        validarReferencias(datos);
        p.aplicar(datos);
        publicaciones.save(p);
        auditar(actual, "PUBLICACION_EDITADA", p.getId(), null);
        return detalle(p);
    }

    /** Criterio 2 (enviar): revalida los datos guardados y pasa de BORRADOR a PENDIENTE_REVISION. */
    @Transactional
    public PublicacionDetalle enviarARevision(UUID id, UsuarioAutenticado actual) {
        Publicacion p = propia(id, actual);
        if (p.getEstado() != EstadoPublicacion.BORRADOR) {
            throw estadoNoPermitido(p, "enviar a revisión");
        }
        validarCompleta(p);
        p.cambiarEstado(EstadoPublicacion.PENDIENTE_REVISION);
        publicaciones.save(p);
        auditar(actual, "PUBLICACION_ENVIADA_REVISION", p.getId(), null);
        return detalle(p);
    }

    @Transactional(readOnly = true)
    public PublicacionDetalle obtener(UUID id, UsuarioAutenticado actual) {
        return detalle(propia(id, actual));
    }

    @Transactional(readOnly = true)
    public List<PublicacionResumen> listarPropias(UsuarioAutenticado actual) {
        return publicaciones.findByAnfitrionIdOrderByFechaActualizacionDesc(actual.id()).stream()
                .map(p -> new PublicacionResumen(p.getId(), p.getTitulo(), p.getCiudad(), p.getEstado(),
                        p.getCapacidad(), p.getPrecioNoche()))
                .toList();
    }

    public List<Servicio> catalogoServicios() {
        return servicios.findAll().stream().sorted(Comparator.comparing(Servicio::getNombre)).toList();
    }

    public List<PoliticaCancelacion> catalogoPoliticas() {
        return politicas.findAll().stream().sorted(Comparator.comparing(PoliticaCancelacion::getNombre)).toList();
    }

    // ---------- helpers ----------

    /** Criterio 3: otro anfitrion recibe 403 y el intento queda auditado (transaccion independiente). */
    private Publicacion propia(UUID id, UsuarioAutenticado actual) {
        Publicacion p = publicaciones.findById(id).orElseThrow(() ->
                new ExcepcionNegocio(TipoError.NO_ENCONTRADO, "PUBLICACION_NO_ENCONTRADA",
                        "La publicación no existe"));
        if (!p.getAnfitrionId().equals(actual.id())) {
            auditoria.registrar(new EventoAuditoria(actual.id(), MODULO, "ACCESO_DENEGADO", ENTIDAD,
                    id.toString(), EventoAuditoria.DENEGADO, "El usuario no es dueño de la publicación"));
            throw new ExcepcionNegocio(TipoError.PROHIBIDO, "ACCESO_DENEGADO",
                    "No tienes permiso para gestionar esta publicación");
        }
        return p;
    }

    private void validarReferencias(DatosPublicacion d) {
        if (!politicas.existsById(d.politicaCancelacionId())) {
            throw new ExcepcionNegocio(TipoError.SOLICITUD_INVALIDA, "POLITICA_CANCELACION_INVALIDA",
                    "La política de cancelación no existe");
        }
        if (!d.serviciosIds().isEmpty() && servicios.findAllById(d.serviciosIds()).size() != d.serviciosIds().size()) {
            throw new ExcepcionNegocio(TipoError.SOLICITUD_INVALIDA, "SERVICIO_INVALIDO",
                    "Alguno de los servicios seleccionados no existe");
        }
    }

    private void validarCompleta(Publicacion p) {
        List<ApiError.DetalleCampo> errores = new ArrayList<>();
        if (vacio(p.getTitulo())) errores.add(new ApiError.DetalleCampo("titulo", "El título es obligatorio"));
        if (p.getCapacidad() <= 0) errores.add(new ApiError.DetalleCampo("capacidad", "La capacidad debe ser mayor que 0"));
        if (vacio(p.getDescripcion())) errores.add(new ApiError.DetalleCampo("descripcion", "La descripción es obligatoria"));
        if (vacio(p.getCiudad())) errores.add(new ApiError.DetalleCampo("ciudad", "La ciudad es obligatoria"));
        if (vacio(p.getUbicacionTextual())) errores.add(new ApiError.DetalleCampo("ubicacionTextual", "La ubicación es obligatoria"));
        if (p.getPrecioNoche() == null || p.getPrecioNoche().signum() <= 0) {
            errores.add(new ApiError.DetalleCampo("precioNoche", "El precio por noche debe ser mayor que 0"));
        }
        if (!errores.isEmpty()) {
            throw new ExcepcionNegocio(TipoError.SOLICITUD_INVALIDA, "PUBLICACION_INCOMPLETA",
                    "La publicación tiene datos inválidos o incompletos", errores);
        }
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private ExcepcionNegocio estadoNoPermitido(Publicacion p, String accion) {
        return new ExcepcionNegocio(TipoError.CONFLICTO, "ESTADO_PUBLICACION_INVALIDO",
                "No se puede " + accion + " una publicación en estado " + p.getEstado());
    }

    private PublicacionDetalle detalle(Publicacion p) {
        return new PublicacionDetalle(p.getId(), p.getAnfitrionId(), p.getPoliticaCancelacionId(), p.getTitulo(),
                p.getDescripcion(), p.getCiudad(), p.getUbicacionTextual(), p.getTipo(), p.getCapacidad(),
                p.getPrecioNoche(), p.getTarifaLimpieza(), p.getReglas(), p.getModoReserva(), p.getEstado(),
                p.getMotivoEstado(),
                new ArrayList<>(p.getServiciosIds()),
                p.getServiciosAdicionales().stream()
                        .map(s -> new PublicacionDetalle.ServicioAdicionalVista(s.getNombre(), s.getCosto())).toList(),
                p.getImagenes().stream().map(i -> i.getRuta()).toList());
    }

    private void auditar(UsuarioAutenticado actual, String accion, UUID id, String motivo) {
        auditoria.registrar(new EventoAuditoria(actual.id(), MODULO, accion, ENTIDAD, id.toString(),
                EventoAuditoria.EXITO, motivo));
    }
}
