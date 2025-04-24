package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the application and enables
 * auto-configuration through the @SpringBootApplication annotation.
 * 
 * This is a migration of the JBoss EAP Kitchensink quickstart to Spring Boot.
 */
@SpringBootApplication
public class KitchensinkApplication {

    /**
     * Main method to bootstrap the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
