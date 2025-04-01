package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the application and configures
 * Spring Boot's auto-configuration, component scanning, and other features.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.kitchensink.repository")
@ComponentScan(basePackages = {
    "com.example.kitchensink.controller",
    "com.example.kitchensink.service",
    "com.example.kitchensink.repository",
    "com.example.kitchensink.config"
})
public class KitchensinkApplication {

    /**
     * Main method that serves as the entry point for the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
