package com.example.kitchensink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI documentation.
 * This class customizes the OpenAPI specification and Swagger UI
 * for the Kitchensink application.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Configures the OpenAPI documentation with custom information.
     *
     * @return the OpenAPI configuration bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kitchensink REST API")
                        .version("1.0.0")
                        .description("REST API for the Kitchensink application, migrated from JBoss EAP to Spring Boot 3")
                        .termsOfService("https://example.com/terms")
                        .contact(new Contact()
                                .name("Development Team")
                                .url("https://example.com")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Default Server URL"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")));
    }
}
