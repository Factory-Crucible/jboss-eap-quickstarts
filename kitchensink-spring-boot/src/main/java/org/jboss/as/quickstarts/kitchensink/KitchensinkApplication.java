package org.jboss.as.quickstarts.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This class serves as the entry point for the application and enables
 * auto-configuration of Spring Boot components.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class KitchensinkApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
