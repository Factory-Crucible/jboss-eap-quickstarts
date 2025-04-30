package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the application and enables
 * auto-configuration, component scanning, and other Spring Boot features.
 * 
 * This replaces the traditional Java EE deployment descriptor approach
 * used in the original JBoss application.
 */
@SpringBootApplication
public class KitchensinkApplication {

    private static final Logger log = LoggerFactory.getLogger(KitchensinkApplication.class);

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.info("Starting Kitchensink Spring Boot Application");
        SpringApplication.run(KitchensinkApplication.class, args);
        log.info("Kitchensink Spring Boot Application started successfully");
    }
}
