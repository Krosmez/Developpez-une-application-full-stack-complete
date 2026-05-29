package com.openclassrooms.mddapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  private static final String BEARER_SCHEME = "bearerAuth";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI().info(new Info().title("MDD API")
                                        .description("API REST du réseau social MDD (Monde de Dév)")
                                        .version("1.0.0")
                                        .contact(new Contact().name("OpenClassrooms P6")))
                        .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                        .components(new Components().addSecuritySchemes(
                            BEARER_SCHEME,
                            new SecurityScheme().name(BEARER_SCHEME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Saisir le token JWT obtenu via /api/v1/auth/login")
                        ));
  }
}
