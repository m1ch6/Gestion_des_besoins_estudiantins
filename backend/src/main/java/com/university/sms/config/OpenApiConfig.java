package com.university.sms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger / OpenAPI 3 pour springdoc-openapi.
 * – Définit le securityScheme "bearerAuth" utilisé dans
 * les contrôleurs via @SecurityRequirement(name = "bearerAuth").
 * – Regroupe les endpoints de l’application sous le même jeu
 * de métadonnées.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Student Management System API", version = "1.0", description = "Gestion des miniprojets et mémoires", contact = @Contact(name = "SMS team", email = "support@university.com")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

    /**
     * Un seul groupe couvrant tout /api/** (par défaut).
     * Si vous souhaitez scinder vos endpoints par modules,
     * ajoutez d’autres GroupedOpenApi.
     */
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("sms")
                .pathsToMatch("/**")
                .build();
    }
}
