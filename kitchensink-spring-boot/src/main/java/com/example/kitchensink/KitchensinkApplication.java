package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This is the entry point for the Spring Boot application that replaces
 * the JBoss EAP kitchensink example.
 */
@SpringBootApplication
@EnableJpaRepositories
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
