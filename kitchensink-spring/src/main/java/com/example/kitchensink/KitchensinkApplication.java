package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the application and enables
 * Spring Boot's auto-configuration, component scanning, and bean registration.
 */
@SpringBootApplication
public class KitchensinkApplication {

    /**
     * Main method that bootstraps the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
