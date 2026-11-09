package co.edu.unbosque.marketplace.usuarios.application;

/** enlaceSimulado solo aparece con app.recuperacion.exponer-enlace=true (perfil dev). */
public record RecuperacionResponse(String mensaje, String enlaceSimulado) {}
