package com.example.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic Spring Boot test class that verifies the application context loads successfully.
 * This test ensures that the Spring Boot configuration is correct and all required
 * beans can be created without errors.
 */
@SpringBootTest
class KitchensinkApplicationTests {

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot be loaded
        // No assertions needed - Spring will fail the test if context doesn't load
    }
}
