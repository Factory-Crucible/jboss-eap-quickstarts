package com.factory.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Kitchensink Spring Boot application.
 * This is the entry point for the application and enables component scanning
 * and auto-configuration.
 */
@SpringBootApplication
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
