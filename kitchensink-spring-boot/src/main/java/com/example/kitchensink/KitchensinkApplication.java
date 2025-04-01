package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * KitchensinkApplication
 * 
 * This is the main entry point for the Spring Boot application.
 * It replaces the traditional Jakarta EE deployment descriptors and bootstrapping.
 * 
 * The application demonstrates a web-enabled database application using:
 * - Spring MVC (replacing JAX-RS and JSF)
 * - Spring Data JPA (replacing JPA/Hibernate manual configuration)
 * - Spring Validation (replacing Bean Validation)
 * - Spring Boot (replacing JBoss EAP container)
 * 
 * Key components of the application:
 * - Model: Defines the Member entity with validation constraints
 * - Repository: Provides data access using Spring Data JPA
 * - Service: Contains business logic for member registration and management
 * - Controller: Exposes REST endpoints and handles web requests
 * - Exception handling: Global exception handling for consistent error responses
 * 
 * This application is a migration of the JBoss EAP Kitchensink quickstart
 * to a modern Spring Boot architecture using Java 21.
 */
@SpringBootApplication
@EnableJpaRepositories
public class KitchensinkApplication {

    /**
     * Main method that serves as the entry point for the Spring Boot application.
     * This replaces the traditional Jakarta EE deployment and server startup.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
    
    /**
     * Configure Cross-Origin Resource Sharing (CORS) for the application.
     * This allows the REST API to be accessed from different origins if needed.
     * 
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
    
    /**
     * The application startup will automatically:
     * 1. Create the database schema based on JPA entities
     * 2. Load initial data from data.sql if present
     * 3. Configure all beans and dependencies
     * 4. Start the embedded web server
     * 
     * Access the application at: http://localhost:8080/
     * Access the H2 console at: http://localhost:8080/h2-console
     * Access the API documentation at: http://localhost:8080/swagger-ui.html
     */
}
