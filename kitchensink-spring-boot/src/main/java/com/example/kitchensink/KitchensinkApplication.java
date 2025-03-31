package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Kitchensink Spring Boot application.
 * This class serves as the entry point for the application and configures
 * Spring Boot's auto-configuration, component scanning, and other features.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.kitchensink.repository")
@EntityScan(basePackages = "com.example.kitchensink.model")
@EnableTransactionManagement
public class KitchensinkApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
