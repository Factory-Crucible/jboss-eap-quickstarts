package org.jboss.eap.quickstarts.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the Spring Boot application.
 * 
 * Migrated from the JBoss EAP Kitchensink quickstart.
 */
@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages = "org.jboss.eap.quickstarts.kitchensink")
public class KitchensinkApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
