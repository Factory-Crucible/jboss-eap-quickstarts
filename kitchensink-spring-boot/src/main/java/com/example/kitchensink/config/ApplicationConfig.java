package com.example.kitchensink.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Application configuration class that sets up common beans and configurations.
 * This class replaces the functionality previously provided by the Resources.java
 * class in the JBoss EAP application, adapting it to Spring Boot patterns.
 */
@Configuration
@EnableJpaAuditing
public class ApplicationConfig implements WebMvcConfigurer {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Provides a Logger factory method to create context-aware loggers.
     * This replaces the CDI producer method in the original JBoss application.
     * 
     * @param name The name of the logger, typically the class name
     * @return A configured Logger instance
     */
    @Bean
    @Description("Produces a Logger instance for dependency injection")
    public Logger loggerFactory(String name) {
        return Logger.getLogger(name);
    }

    /**
     * Configures the Jakarta Bean Validation factory.
     * This ensures consistent validation behavior across the application.
     * 
     * @return Configured validator factory bean
     */
    @Bean
    @Description("Configures the Bean Validation factory")
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Exposes the EntityManager as a bean for dependency injection.
     * This replaces the CDI producer in the original JBoss application.
     * 
     * @return The JPA EntityManager
     */
    @Bean
    @Description("Exposes the EntityManager for dependency injection")
    public EntityManager entityManager() {
        return entityManager;
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for the application.
     * This allows the API to be accessed from different origins if needed.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
