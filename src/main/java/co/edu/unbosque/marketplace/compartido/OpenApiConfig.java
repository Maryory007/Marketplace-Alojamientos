package co.edu.unbosque.marketplace.compartido;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger UI: http://localhost:8080/swagger-ui.html. El boton Authorize recibe el token del login. */
@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI marketplaceOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Marketplace de alojamientos temporales").version("v1"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
