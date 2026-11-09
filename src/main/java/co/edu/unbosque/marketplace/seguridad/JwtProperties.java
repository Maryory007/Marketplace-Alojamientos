package co.edu.unbosque.marketplace.seguridad;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMinutes, String issuer) {}
