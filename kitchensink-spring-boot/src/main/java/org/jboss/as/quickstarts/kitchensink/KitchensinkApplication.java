package org.jboss.as.quickstarts.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * Migrated from the original JBoss EAP Kitchensink quickstart.
 */
@SpringBootApplication
@EnableJpaRepositories
public class KitchensinkApplication {

    private static final Logger log = LoggerFactory.getLogger(KitchensinkApplication.class);

    /**
     * Main method to bootstrap the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
    
    /**
     * CommandLineRunner bean to log application startup information.
     * 
     * @return CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner init() {
        return args -> {
            log.info("Kitchensink Spring Boot Application Started");
            log.info("Access the REST API at: http://localhost:8080/api/members");
            log.info("Access the H2 Console at: http://localhost:8080/h2-console (if enabled)");
        };
    }
}
