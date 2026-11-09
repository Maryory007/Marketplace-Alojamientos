package co.edu.unbosque.marketplace.publicaciones.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publicacion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "anfitrion_id", nullable = false, updatable = false)
    private UUID anfitrionId;

    @Column(name = "politica_cancelacion_id", nullable = false)
    private UUID politicaCancelacionId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @Column(name = "ubicacion_textual", nullable = false, length = 255)
    private String ubicacionTextual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlojamiento tipo;

    @Column(nullable = false)
    private int capacidad;

    @Column(name = "precio_noche", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioNoche;

    @Column(name = "tarifa_limpieza", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaLimpieza;

    @Column(nullable = false, columnDefinition = "text")
    private String reglas;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_reserva", nullable = false, length = 20)
    private ModoReserva modoReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPublicacion estado;

    @Column(name = "motivo_estado", length = 500)
    private String motivoEstado;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false, insertable = false)
    private Instant fechaActualizacion;

    @ElementCollection
    @CollectionTable(name = "publicacion_servicio", joinColumns = @JoinColumn(name = "publicacion_id"))
    @Column(name = "servicio_id", nullable = false)
    private Set<UUID> serviciosIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "servicio_adicional", joinColumns = @JoinColumn(name = "publicacion_id"))
    private List<ServicioAdicionalPublicacion> serviciosAdicionales = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "imagen", joinColumns = @JoinColumn(name = "publicacion_id"))
    @OrderBy("orden ASC")
    private List<ImagenPublicacion> imagenes = new ArrayList<>();

    /** Toda publicacion nueva nace en BORRADOR. */
    public Publicacion(UUID anfitrionId) {
        this.anfitrionId = anfitrionId;
        this.estado = EstadoPublicacion.BORRADOR;
    }

    public void aplicar(DatosPublicacion d) {
        this.politicaCancelacionId = d.politicaCancelacionId();
        this.titulo = d.titulo();
        this.descripcion = d.descripcion();
        this.ciudad = d.ciudad();
        this.ubicacionTextual = d.ubicacionTextual();
        this.tipo = d.tipo();
        this.capacidad = d.capacidad();
        this.precioNoche = d.precioNoche();
        this.tarifaLimpieza = d.tarifaLimpieza();
        this.reglas = d.reglas();
        this.modoReserva = d.modoReserva();

        this.serviciosIds.clear();
        this.serviciosIds.addAll(d.serviciosIds());

        this.serviciosAdicionales.clear();
        this.serviciosAdicionales.addAll(d.serviciosAdicionales());

        this.imagenes.clear();
        for (int i = 0; i < d.imagenes().size(); i++) {
            this.imagenes.add(new ImagenPublicacion(d.imagenes().get(i), i));
        }
        this.fechaActualizacion = Instant.now();
    }

    public void cambiarEstado(EstadoPublicacion nuevo) {
        this.estado = nuevo;
        this.fechaActualizacion = Instant.now();
    }
}
