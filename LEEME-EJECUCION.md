# Cómo ejecutar

1. Base de datos:  `docker compose up -d`  (PostgreSQL en el puerto 5432; si ya tienes uno local, detenlo).
2. Aplicación con el perfil `dev` (crea usuarios de prueba, contraseña `Test1234*`):
   - Eclipse/STS: clic derecho en el proyecto > Run As > Run Configurations > Spring Boot App >
     pestaña "Spring Boot" > campo **Profile**: `dev`  (o en Arguments > VM arguments: `-Dspring.profiles.active=dev`)
   - Terminal: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
3. Swagger: http://localhost:8080/swagger-ui.html

Para endpoints protegidos: `POST /api/auth/login`, copia el `token` y pulsa **Authorize** en Swagger.
Usuarios dev: admin@marketplace.test, anfitrion@marketplace.test, huesped@marketplace.test (todos `Test1234*`).

## Registro y recuperación
- `POST /api/auth/registro` (rol HUESPED o ANFITRION). Política de contraseña: 8-128 caracteres con mayúscula,
  minúscula, número y símbolo. Correo repetido -> 409 `CORREO_YA_REGISTRADO`.
- `POST /api/auth/recuperar-contrasena` -> el enlace simulado se escribe en el log (`[CORREO SIMULADO]`), queda en la
  tabla `recuperacion_contrasena` (solo hash) y en auditoría. En perfil `dev` también viene en la respuesta (`enlaceSimulado`).
- `POST /api/auth/restablecer-contrasena` con `token` (lo que va después de `?token=`) y `nuevaContrasena`.
  El enlace vence a los 30 min y sirve una sola vez.
- Nueva migración: `V7__recuperacion_contrasena.sql`.

## Lombok en Eclipse/STS
Hay que instalarlo una vez: `java -jar lombok.jar` > elegir Eclipse > Install/Update > reiniciar >
Maven > Update Project (Force Update) + Project > Clean.

## Corrección anterior
`JwtProperties` no estaba registrada como bean; se agregó `@EnableConfigurationProperties(JwtProperties.class)` en `SecurityConfig`.
