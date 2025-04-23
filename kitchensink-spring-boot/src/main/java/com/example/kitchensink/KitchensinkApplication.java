package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the application.
 * 
 * Migrated from JBoss EAP Quickstarts kitchensink example.
 */
@SpringBootApplication
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
