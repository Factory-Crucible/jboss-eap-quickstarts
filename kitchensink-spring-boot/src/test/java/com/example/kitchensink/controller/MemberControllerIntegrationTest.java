package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MemberController.
 * Tests the full request-response cycle through the actual REST API endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class MemberControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    private String baseUrl;
    private Member testMember;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/members";
        
        // Create a test member for use in tests
        testMember = new Member();
        testMember.setName("Test User");
        testMember.setEmail("test.user@example.com");
        testMember.setPhoneNumber("1234567890");
        
        // Clear any existing data and save our test member
        memberRepository.deleteAll();
        memberRepository.save(testMember);
    }

    @Test
    void listAllMembers_ShouldReturnAllMembers() {
        // Given: A member exists in the database (from setUp)
        
        // When: GET request to /api/members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {}
        );
        
        // Then: Status is 200 OK and response contains the member
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        Member returnedMember = response.getBody().get(0);
        assertEquals(testMember.getName(), returnedMember.getName());
        assertEquals(testMember.getEmail(), returnedMember.getEmail());
    }

    @Test
    void getMemberById_WithValidId_ShouldReturnMember() {
        // Given: A member exists in the database (from setUp)
        Long memberId = testMember.getId();
        
        // When: GET request to /api/members/{id}
        ResponseEntity<Member> response = restTemplate.getForEntity(
                baseUrl + "/" + memberId,
                Member.class
        );
        
        // Then: Status is 200 OK and response contains the correct member
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testMember.getId(), response.getBody().getId());
        assertEquals(testMember.getName(), response.getBody().getName());
        assertEquals(testMember.getEmail(), response.getBody().getEmail());
    }

    @Test
    void getMemberById_WithInvalidId_ShouldReturn404() {
        // Given: A non-existent member ID
        Long nonExistentId = 999L;
        
        // When: GET request to /api/members/{id} with non-existent ID
        ResponseEntity<Member> response = restTemplate.getForEntity(
                baseUrl + "/" + nonExistentId,
                Member.class
        );
        
        // Then: Status is 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createMember_WithValidData_ShouldCreateAndReturnMember() {
        // Given: A valid member object
        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("new.user@example.com");
        newMember.setPhoneNumber("9876543210");
        
        // When: POST request to /api/members
        ResponseEntity<Member> response = restTemplate.postForEntity(
                baseUrl,
                newMember,
                Member.class
        );
        
        // Then: Status is 201 Created and response contains the created member
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(newMember.getName(), response.getBody().getName());
        assertEquals(newMember.getEmail(), response.getBody().getEmail());
        
        // Verify the member was actually saved in the database
        assertTrue(memberRepository.findByEmail("new.user@example.com").isPresent());
    }

    @Test
    void createMember_WithInvalidData_ShouldReturn400WithErrors() {
        // Given: An invalid member object (missing required fields)
        Member invalidMember = new Member();
        invalidMember.setName(""); // Invalid: empty name
        invalidMember.setEmail("not-an-email"); // Invalid: not an email format
        invalidMember.setPhoneNumber("123"); // Invalid: too short
        
        // When: POST request to /api/members
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        // Then: Status is 400 Bad Request and response contains validation errors
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Verify specific validation errors are present
        Map<String, String> errors = response.getBody();
        assertTrue(errors.containsKey("name") || errors.containsKey("email") || errors.containsKey("phoneNumber"));
    }

    @Test
    void createMember_WithDuplicateEmail_ShouldReturn409Conflict() {
        // Given: A member with an email that already exists
        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Another User");
        duplicateEmailMember.setEmail(testMember.getEmail()); // Same email as existing member
        duplicateEmailMember.setPhoneNumber("5555555555");
        
        // When: POST request to /api/members
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(duplicateEmailMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        // Then: Status is 409 Conflict and response contains error message
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("email"));
        assertEquals("Email taken", response.getBody().get("email"));
    }

    @Test
    void createMember_WithInvalidNameContainingNumbers_ShouldReturnValidationError() {
        // Given: A member with a name containing numbers (violates pattern constraint)
        Member invalidNameMember = new Member();
        invalidNameMember.setName("User123"); // Invalid: contains numbers
        invalidNameMember.setEmail("valid.email@example.com");
        invalidNameMember.setPhoneNumber("1234567890");
        
        // When: POST request to /api/members
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(invalidNameMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        // Then: Status is 400 Bad Request and response contains name validation error
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("name"));
        assertEquals("Must not contain numbers", response.getBody().get("name"));
    }
}
