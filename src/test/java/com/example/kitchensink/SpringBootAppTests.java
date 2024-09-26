package com.example.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SpringBootAppTests {

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot start
    }

    // Add any common setup methods or utility methods here that might be useful across multiple test classes

    // For example:
    // @BeforeEach
    // void setUp() {
    //     // Common setup code
    // }

    // @AfterEach
    // void tearDown() {
    //     // Common teardown code
    // }

    // protected void assertMemberEquals(Member expected, Member actual) {
    //     assertEquals(expected.getId(), actual.getId());
    //     assertEquals(expected.getName(), actual.getName());
    //     assertEquals(expected.getEmail(), actual.getEmail());
    //     assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    // }
}