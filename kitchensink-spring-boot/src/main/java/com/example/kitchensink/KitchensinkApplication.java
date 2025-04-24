package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * 
 * This class serves as the entry point for the Spring Boot application.
 * The @SpringBootApplication annotation enables auto-configuration and
 * component scanning in the current package and its sub-packages.
 */
@SpringBootApplication
public class KitchensinkApplication {

    /**
     * Main method that bootstraps the Spring Boot application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
