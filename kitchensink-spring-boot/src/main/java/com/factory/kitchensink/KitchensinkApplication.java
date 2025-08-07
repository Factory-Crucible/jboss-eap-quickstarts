package com.factory.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Spring Boot version of the kitchensink application.
 * This is a migration of the original JBoss EAP kitchensink quickstart application
 * from Jakarta EE to Spring Boot 3 with Java 21.
 */
@SpringBootApplication
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
