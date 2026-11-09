package co.edu.unbosque.marketplace.publicaciones.domain;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Catalogo de servicios que puede marcar un anfitrion (solo lectura). */
@Entity
@Table(name = "servicio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Servicio {

    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String nombre;
}
