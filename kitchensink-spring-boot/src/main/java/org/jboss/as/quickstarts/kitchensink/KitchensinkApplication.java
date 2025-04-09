package org.jboss.as.quickstarts.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * This is the entry point for the modernized version of the JBoss EAP Kitchensink quickstart.
 * 
 * The application supports both JPA (for relational databases) and MongoDB.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.jboss.as.quickstarts.kitchensink.repository.jpa")
@EnableMongoRepositories(basePackages = "org.jboss.as.quickstarts.kitchensink.repository.mongo")
public class KitchensinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
