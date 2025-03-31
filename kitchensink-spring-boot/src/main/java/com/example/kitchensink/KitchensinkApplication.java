package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main application class for the Kitchensink Spring Boot application.
 * This class serves as the entry point for the application and configures
 * component scanning, auto-configuration, and additional features.
 * 
 * The @SpringBootApplication annotation combines:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings
 * - @ComponentScan: Tells Spring to look for components in the current package and below
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.kitchensink.repository.jpa")
@EnableMongoRepositories(basePackages = "com.example.kitchensink.repository.mongo")
public class KitchensinkApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
