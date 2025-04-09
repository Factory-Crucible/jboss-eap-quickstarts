package com.example.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the Kitchensink Spring Boot application.
 * This test verifies that the Spring application context loads correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
class KitchensinkApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private Environment environment;

    /**
     * Verifies that the application context loads successfully.
     * This is a basic smoke test to ensure the Spring Boot application
     * is configured correctly.
     */
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }
    
    /**
     * Verifies that the test profile is active.
     * This ensures that the application is using the correct
     * configuration properties for testing.
     */
    @Test
    void testProfileIsActive() {
        assertThat(environment.getActiveProfiles()).contains("test");
    }
    
    /**
     * Verifies that the H2 database is configured correctly.
     * This ensures that the application can connect to the test database.
     */
    @Test
    void databaseConfigurationLoaded() {
        assertThat(environment.getProperty("spring.datasource.url"))
            .contains("jdbc:h2:mem:testdb");
        assertThat(environment.getProperty("spring.datasource.username"))
            .isEqualTo("sa");
    }
    
    /**
     * Verifies that the Hibernate configuration is loaded correctly.
     * This ensures that JPA is configured properly for testing.
     */
    @Test
    void hibernateConfigurationLoaded() {
        assertThat(environment.getProperty("spring.jpa.hibernate.ddl-auto"))
            .isEqualTo("create-drop");
        assertThat(environment.getProperty("spring.jpa.show-sql"))
            .isEqualTo("true");
    }
}
