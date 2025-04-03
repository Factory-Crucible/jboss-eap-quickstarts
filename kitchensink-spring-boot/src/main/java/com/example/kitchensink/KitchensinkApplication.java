package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This is the entry point for the Spring Boot application.
 * 
 * This application is a migration of the JBoss EAP Kitchensink quickstart
 * to a modern Spring Boot application using Java 21.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.kitchensink.repository")
@EnableTransactionManagement
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
