package com.example.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Kitchensink Spring Boot application.
 * This is the entry point for the application and replaces the traditional
 * Java EE deployment descriptors.
 * 
 * The @SpringBootApplication annotation is a convenience annotation that adds:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to add beans based on classpath settings
 * - @ComponentScan: Tells Spring to look for components in the current package and below
 */
@SpringBootApplication
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
