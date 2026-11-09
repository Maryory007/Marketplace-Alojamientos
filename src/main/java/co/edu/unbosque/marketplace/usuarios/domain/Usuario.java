package co.edu.unbosque.marketplace.usuarios.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 160)
    private String correo;

    @Column(length = 30)
    private String telefono;

    @Column(name = "contrasena_hash", nullable = false)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", nullable = false, length = 20)
    private EstadoCuenta estadoCuenta;

    @Column(name = "fecha_registro", nullable = false, insertable = false, updatable = false)
    private Instant fechaRegistro;

    public Usuario(String nombre, String correo, String telefono, String contrasenaHash, Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
        this.estadoCuenta = EstadoCuenta.ACTIVA;
    }

    public void cambiarContrasena(String nuevoHash) {
        this.contrasenaHash = nuevoHash;
    }
}
