package com.factory.kitchensink;

import com.factory.kitchensink.model.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Spring Boot Kitchensink application.
 * 
 * <p>This test class replaces the original Arquillian-based tests in the JBoss EAP kitchensink application.
 * The original tests (MemberRegistrationIT and RemoteMemberRegistrationIT) used Arquillian to deploy
 * the application to a JBoss EAP container and then run tests against it.</p>
 * 
 * <p>Migration mapping:</p>
 * <ul>
 *   <li>Original: Arquillian @Deployment to create and deploy a test archive</li>
 *   <li>Spring Boot: @SpringBootTest with webEnvironment to start the application</li>
 * </ul>
 * <ul>
 *   <li>Original: CDI @Inject to inject components</li>
 *   <li>Spring Boot: @Autowired to inject components</li>
 * </ul>
 * <ul>
 *   <li>Original: Manual REST client creation</li>
 *   <li>Spring Boot: TestRestTemplate for HTTP requests</li>
 * </ul>
 * <ul>
 *   <li>Original: JUnit 4 with @Test</li>
 *   <li>Spring Boot: JUnit 5 with @Test, @DisplayName, etc.</li>
 * </ul>
 * 
 * <p>This test class is more comprehensive than the original, covering all REST endpoints,
 * validation scenarios, and error handling.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class KitchensinkApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Base URL for the REST API.
     * 
     * @return the base URL for the REST API
     */
    private String getBaseUrl() {
        return "http://localhost:" + port + "/kitchensink/api/members";
    }

    /**
     * Test that the application context loads successfully.
     * 
     * This is a basic test to ensure that the Spring Boot application starts up correctly.
     * There's no direct equivalent in the original Arquillian tests, which assumed
     * the application was already deployed.
     */
    @Test
    @DisplayName("Application context loads successfully")
    @Order(1)
    void contextLoads() {
        // If this test runs, it means the application context loaded successfully
        assertThat(restTemplate).isNotNull();
    }

    /**
     * Test retrieving all members.
     * 
     * This test verifies that the GET /api/members endpoint returns the expected list of members.
     * The original kitchensink application had a similar test in the Arquillian test suite,
     * but it was using a different approach with JAX-RS client.
     */
    @Test
    @DisplayName("GET /api/members returns all members")
    @Order(2)
    void getAllMembersTest() {
        // When: We request all members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {});
        
        // Then: We should get a successful response with at least one member
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        
        // And: The response should include the initial member from data.sql
        boolean foundInitialMember = response.getBody().stream()
                .anyMatch(member -> "john.smith@mailinator.com".equals(member.getEmail()));
        assertThat(foundInitialMember).isTrue();
    }

    /**
     * Test retrieving a member by ID.
     * 
     * This test verifies that the GET /api/members/{id} endpoint returns the expected member.
     * The original kitchensink application didn't have a direct equivalent test for this endpoint.
     */
    @Test
    @DisplayName("GET /api/members/{id} returns the member with the given ID")
    @Order(3)
    void getMemberByIdTest() {
        // Given: We know the ID of an existing member (assuming ID 1 exists from data.sql)
        Long memberId = 1L;
        
        // When: We request the member by ID
        ResponseEntity<Member> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + memberId,
                Member.class);
        
        // Then: We should get a successful response with the correct member
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(memberId);
        assertThat(response.getBody().getEmail()).isEqualTo("john.smith@mailinator.com");
    }

    /**
     * Test retrieving a non-existent member.
     * 
     * This test verifies that the GET /api/members/{id} endpoint returns a 404 Not Found
     * response when the requested member doesn't exist.
     * The original kitchensink application didn't have a direct equivalent test for this scenario.
     */
    @Test
    @DisplayName("GET /api/members/{id} returns 404 for non-existent member")
    @Order(4)
    void getNonExistentMemberTest() {
        // Given: A non-existent member ID
        Long nonExistentId = 999L;
        
        // When: We request the non-existent member
        ResponseEntity<Member> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + nonExistentId,
                Member.class);
        
        // Then: We should get a 404 Not Found response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test creating a new member with valid data.
     * 
     * This test verifies that the POST /api/members endpoint successfully creates a new member
     * when provided with valid data. This is similar to the registerMember test in the original
     * Arquillian test suite, but uses Spring's TestRestTemplate instead of JAX-RS client.
     */
    @Test
    @DisplayName("POST /api/members creates a new member with valid data")
    @Order(5)
    void createMemberWithValidDataTest() {
        // Given: A valid member
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane.doe@example.com");
        newMember.setPhoneNumber("2025551234");
        
        // When: We create the member
        ResponseEntity<Member> response = restTemplate.postForEntity(
                getBaseUrl(),
                newMember,
                Member.class);
        
        // Then: We should get a 201 Created response with the created member
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Jane Doe");
        assertThat(response.getBody().getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(response.getBody().getPhoneNumber()).isEqualTo("2025551234");
    }

    /**
     * Test creating a member with invalid data.
     * 
     * This test verifies that the POST /api/members endpoint returns a 400 Bad Request
     * response when provided with invalid data. The original kitchensink application
     * had validation in the REST service, but didn't have a specific test for this scenario.
     */
    @Test
    @DisplayName("POST /api/members returns 400 for invalid data")
    @Order(6)
    void createMemberWithInvalidDataTest() {
        // Given: An invalid member (missing required fields)
        Member invalidMember = new Member();
        invalidMember.setName(""); // Invalid: name is required and must be at least 1 character
        invalidMember.setEmail("not-an-email"); // Invalid: not a valid email format
        invalidMember.setPhoneNumber("123"); // Invalid: phone number must be 10-12 digits
        
        // When: We try to create the invalid member
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {});
        
        // Then: We should get a 400 Bad Request response with validation errors
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        // And: The response should contain validation errors for each invalid field
        Map<String, String> errors = response.getBody();
        assertThat(errors).containsKey("name");
        assertThat(errors).containsKey("email");
        assertThat(errors).containsKey("phoneNumber");
    }

    /**
     * Test creating a member with a duplicate email.
     * 
     * This test verifies that the POST /api/members endpoint returns a 409 Conflict
     * response when trying to create a member with an email that already exists.
     * The original kitchensink application had similar validation in the REST service,
     * but didn't have a specific test for this scenario.
     */
    @Test
    @DisplayName("POST /api/members returns 409 for duplicate email")
    @Order(7)
    void createMemberWithDuplicateEmailTest() {
        // Given: A member with an email that already exists
        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Another John Smith");
        duplicateEmailMember.setEmail("john.smith@mailinator.com"); // This email already exists in data.sql
        duplicateEmailMember.setPhoneNumber("2025559876");
        
        // When: We try to create the member with a duplicate email
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(duplicateEmailMember),
                new ParameterizedTypeReference<Map<String, String>>() {});
        
        // Then: We should get a 409 Conflict response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        
        // And: The response should indicate that the email is already taken
        Map<String, String> errors = response.getBody();
        assertThat(errors).containsKey("email");
        assertThat(errors.get("email")).contains("already exists");
    }

    /**
     * Test creating multiple valid members and then retrieving them.
     * 
     * This test verifies that multiple members can be created and then retrieved.
     * It's a more comprehensive test that combines creation and retrieval operations.
     */
    @Test
    @DisplayName("Create multiple members and then retrieve them")
    @Order(8)
    void createAndRetrieveMultipleMembersTest() {
        // Given: Two new valid members
        Member member1 = new Member();
        member1.setName("Alice Johnson");
        member1.setEmail("alice.johnson@example.com");
        member1.setPhoneNumber("3035551234");
        
        Member member2 = new Member();
        member2.setName("Bob Williams");
        member2.setEmail("bob.williams@example.com");
        member2.setPhoneNumber("4045556789");
        
        // When: We create both members
        restTemplate.postForEntity(getBaseUrl(), member1, Member.class);
        restTemplate.postForEntity(getBaseUrl(), member2, Member.class);
        
        // And: We retrieve all members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {});
        
        // Then: We should get a successful response with all members including our new ones
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        List<Member> members = response.getBody();
        
        // And: The response should include our newly created members
        boolean foundMember1 = members.stream()
                .anyMatch(m -> "alice.johnson@example.com".equals(m.getEmail()));
        boolean foundMember2 = members.stream()
                .anyMatch(m -> "bob.williams@example.com".equals(m.getEmail()));
        
        assertThat(foundMember1).isTrue();
        assertThat(foundMember2).isTrue();
    }

    /**
     * Test validation for a member with a name containing numbers.
     * 
     * This test verifies that the validation constraint that prevents names from containing
     * numbers is working correctly. This is a specific validation rule from the original
     * kitchensink application that we've preserved in our migration.
     */
    @Test
    @DisplayName("Validation rejects names containing numbers")
    @Order(9)
    void nameWithNumbersValidationTest() {
        // Given: A member with a name containing numbers
        Member invalidMember = new Member();
        invalidMember.setName("John123"); // Invalid: name must not contain numbers
        invalidMember.setEmail("john123@example.com");
        invalidMember.setPhoneNumber("5055551234");
        
        // When: We try to create the member
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {});
        
        // Then: We should get a 400 Bad Request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        // And: The response should contain a validation error for the name field
        Map<String, String> errors = response.getBody();
        assertThat(errors).containsKey("name");
        assertThat(errors.get("name")).contains("Must not contain numbers");
    }

    /**
     * Test phone number validation.
     * 
     * This test verifies that the validation constraints for phone numbers are working correctly.
     * The original kitchensink application required phone numbers to be 10-12 digits.
     */
    @Test
    @DisplayName("Validation enforces phone number format")
    @Order(10)
    void phoneNumberValidationTest() {
        // Given: A member with an invalid phone number (contains letters)
        Member invalidMember = new Member();
        invalidMember.setName("Charlie Brown");
        invalidMember.setEmail("charlie.brown@example.com");
        invalidMember.setPhoneNumber("555-CALL-ME"); // Invalid: must be 10-12 digits
        
        // When: We try to create the member
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {});
        
        // Then: We should get a 400 Bad Request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        // And: The response should contain a validation error for the phone number field
        Map<String, String> errors = response.getBody();
        assertThat(errors).containsKey("phoneNumber");
    }

    /**
     * Test email validation.
     * 
     * This test verifies that the email validation is working correctly.
     * The original kitchensink application required valid email formats.
     */
    @Test
    @DisplayName("Validation enforces email format")
    @Order(11)
    void emailValidationTest() {
        // Given: A member with an invalid email format
        Member invalidMember = new Member();
        invalidMember.setName("David Miller");
        invalidMember.setEmail("not-an-email-address"); // Invalid: not a valid email format
        invalidMember.setPhoneNumber("6065551234");
        
        // When: We try to create the member
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {});
        
        // Then: We should get a 400 Bad Request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        
        // And: The response should contain a validation error for the email field
        Map<String, String> errors = response.getBody();
        assertThat(errors).containsKey("email");
    }

    /**
     * Test malformed JSON handling.
     * 
     * This test verifies that the application correctly handles malformed JSON in requests.
     * This is an enhancement over the original kitchensink application, which didn't have
     * specific handling for this scenario.
     */
    @Test
    @DisplayName("Application handles malformed JSON correctly")
    @Order(12)
    void malformedJsonTest() {
        // Given: Malformed JSON
        String malformedJson = "{\"name\":\"Malformed, \"email\":\"missing.quotes@example.com\"}";
        
        // When: We send a request with malformed JSON
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(malformedJson, Map.of("Content-Type", "application/json")),
                new ParameterizedTypeReference<Map<String, String>>() {});
        
        // Then: We should get a 400 Bad Request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
