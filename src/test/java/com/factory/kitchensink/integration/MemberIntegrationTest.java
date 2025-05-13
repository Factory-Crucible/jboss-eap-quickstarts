package com.factory.kitchensink.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.dto.MemberDTO;
import com.factory.kitchensink.exception.ErrorResponse;

/**
 * Integration tests for the Member API.
 * Tests the full application flow from HTTP request to database and back.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Test retrieving all members.
     * This test verifies that the GET /api/members endpoint returns all members.
     */
    @Test
    public void getAllMembers_ShouldReturnAllMembers() {
        // Act
        ResponseEntity<List<MemberDTO>> response = restTemplate.exchange(
                "/kitchensink/api/members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MemberDTO>>() {});
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MemberDTO> members = response.getBody();
        assertNotNull(members);
        assertThat(members.size()).isGreaterThanOrEqualTo(5); // We expect at least 5 members from data.sql
        
        // Verify some of the members
        boolean foundJohn = false;
        boolean foundJane = false;
        
        for (MemberDTO member : members) {
            if ("John Smith".equals(member.getName()) && "john.smith@example.com".equals(member.getEmail())) {
                foundJohn = true;
            }
            if ("Jane Doe".equals(member.getName()) && "jane.doe@example.com".equals(member.getEmail())) {
                foundJane = true;
            }
        }
        
        assertTrue(foundJohn, "John Smith should be in the members list");
        assertTrue(foundJane, "Jane Doe should be in the members list");
    }
    
    /**
     * Test retrieving a member by ID.
     * This test verifies that the GET /api/members/{id} endpoint returns the correct member.
     */
    @Test
    public void getMemberById_ShouldReturnMember_WhenMemberExists() {
        // Arrange
        // First, get all members to find a valid ID
        ResponseEntity<List<MemberDTO>> allMembersResponse = restTemplate.exchange(
                "/kitchensink/api/members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MemberDTO>>() {});
        
        List<MemberDTO> allMembers = allMembersResponse.getBody();
        assertNotNull(allMembers);
        assertThat(allMembers).isNotEmpty();
        
        Long memberId = allMembers.get(0).getId();
        
        // Act
        ResponseEntity<MemberDTO> response = restTemplate.getForEntity(
                "/kitchensink/api/members/{id}",
                MemberDTO.class,
                memberId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MemberDTO member = response.getBody();
        assertNotNull(member);
        assertEquals(memberId, member.getId());
        assertNotNull(member.getName());
        assertNotNull(member.getEmail());
        assertNotNull(member.getPhoneNumber());
    }
    
    /**
     * Test retrieving a member by ID when the member does not exist.
     * This test verifies that the GET /api/members/{id} endpoint returns a 404 status.
     */
    @Test
    public void getMemberById_ShouldReturnNotFound_WhenMemberDoesNotExist() {
        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/kitchensink/api/members/{id}",
                ErrorResponse.class,
                999L); // Using a non-existent ID
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("NOT_FOUND", error.getCode());
        assertTrue(error.getMessage().contains("Member not found"));
    }
    
    /**
     * Test retrieving a member by email.
     * This test verifies that the GET /api/members/email/{email} endpoint returns the correct member.
     */
    @Test
    public void getMemberByEmail_ShouldReturnMember_WhenMemberExists() {
        // Arrange
        String email = "john.smith@example.com"; // From data.sql
        
        // Act
        ResponseEntity<MemberDTO> response = restTemplate.getForEntity(
                "/kitchensink/api/members/email/{email}",
                MemberDTO.class,
                email);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MemberDTO member = response.getBody();
        assertNotNull(member);
        assertEquals(email, member.getEmail());
        assertEquals("John Smith", member.getName());
        assertEquals("2125551212", member.getPhoneNumber());
    }
    
    /**
     * Test retrieving a member by email when the member does not exist.
     * This test verifies that the GET /api/members/email/{email} endpoint returns a 404 status.
     */
    @Test
    public void getMemberByEmail_ShouldReturnNotFound_WhenMemberDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        
        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/kitchensink/api/members/email/{email}",
                ErrorResponse.class,
                email);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("NOT_FOUND", error.getCode());
        assertTrue(error.getMessage().contains("Member not found"));
    }
    
    /**
     * Test creating a new member.
     * This test verifies that the POST /api/members endpoint creates a new member.
     */
    @Test
    public void createMember_ShouldCreateMember_WhenDataIsValid() {
        // Arrange
        CreateMemberDTO newMember = new CreateMemberDTO(
                "Test User",
                "test.user@example.com",
                "5551234567");
        
        // Act
        ResponseEntity<MemberDTO> response = restTemplate.postForEntity(
                "/kitchensink/api/members",
                newMember,
                MemberDTO.class);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        MemberDTO createdMember = response.getBody();
        assertNotNull(createdMember);
        assertNotNull(createdMember.getId());
        assertEquals(newMember.getName(), createdMember.getName());
        assertEquals(newMember.getEmail(), createdMember.getEmail());
        assertEquals(newMember.getPhoneNumber(), createdMember.getPhoneNumber());
        
        // Verify that the Location header is set correctly
        String locationHeader = response.getHeaders().getFirst("Location");
        assertNotNull(locationHeader);
        assertTrue(locationHeader.endsWith("/api/members/" + createdMember.getId()));
        
        // Verify that the member can be retrieved
        ResponseEntity<MemberDTO> getResponse = restTemplate.getForEntity(
                "/kitchensink/api/members/{id}",
                MemberDTO.class,
                createdMember.getId());
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        MemberDTO retrievedMember = getResponse.getBody();
        assertNotNull(retrievedMember);
        assertEquals(createdMember.getId(), retrievedMember.getId());
        assertEquals(createdMember.getName(), retrievedMember.getName());
        assertEquals(createdMember.getEmail(), retrievedMember.getEmail());
        assertEquals(createdMember.getPhoneNumber(), retrievedMember.getPhoneNumber());
    }
    
    /**
     * Test creating a member with an email that already exists.
     * This test verifies that the POST /api/members endpoint returns a 409 status.
     */
    @Test
    public void createMember_ShouldReturnConflict_WhenEmailAlreadyExists() {
        // Arrange
        CreateMemberDTO newMember = new CreateMemberDTO(
                "Duplicate Email",
                "john.smith@example.com", // This email already exists in data.sql
                "5559876543");
        
        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/kitchensink/api/members",
                newMember,
                ErrorResponse.class);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("CONFLICT", error.getCode());
        assertTrue(error.getMessage().contains("Email already exists"));
    }
    
    /**
     * Test creating a member with invalid data.
     * This test verifies that the POST /api/members endpoint returns a 400 status.
     */
    @Test
    public void createMember_ShouldReturnBadRequest_WhenDataIsInvalid() {
        // Arrange - Empty name, invalid email, short phone number
        CreateMemberDTO invalidMember = new CreateMemberDTO(
                "",
                "not-an-email",
                "123");
        
        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/kitchensink/api/members",
                invalidMember,
                ErrorResponse.class);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = response.getBody();
        assertNotNull(error);
        assertEquals("VALIDATION_FAILED", error.getCode());
        assertTrue(error.getMessage().contains("Validation failed"));
        
        // Verify that field-specific errors are returned
        assertNotNull(error.getErrors());
        assertTrue(error.getErrors().containsKey("name"));
        assertTrue(error.getErrors().containsKey("email"));
        assertTrue(error.getErrors().containsKey("phoneNumber"));
    }
    
    /**
     * Test the full member registration flow.
     * This test verifies the end-to-end flow of registering a member and retrieving it.
     */
    @Test
    public void memberRegistrationFlow_ShouldWorkEndToEnd() {
        // 1. Create a unique member
        String uniqueEmail = "unique." + System.currentTimeMillis() + "@example.com";
        CreateMemberDTO newMember = new CreateMemberDTO(
                "Flow Test User",
                uniqueEmail,
                "9998887777");
        
        ResponseEntity<MemberDTO> createResponse = restTemplate.postForEntity(
                "/kitchensink/api/members",
                newMember,
                MemberDTO.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        MemberDTO createdMember = createResponse.getBody();
        assertNotNull(createdMember);
        Long memberId = createdMember.getId();
        
        // 2. Retrieve the member by ID
        ResponseEntity<MemberDTO> getByIdResponse = restTemplate.getForEntity(
                "/kitchensink/api/members/{id}",
                MemberDTO.class,
                memberId);
        
        assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
        MemberDTO memberById = getByIdResponse.getBody();
        assertNotNull(memberById);
        assertEquals(memberId, memberById.getId());
        
        // 3. Retrieve the member by email
        ResponseEntity<MemberDTO> getByEmailResponse = restTemplate.getForEntity(
                "/kitchensink/api/members/email/{email}",
                MemberDTO.class,
                uniqueEmail);
        
        assertEquals(HttpStatus.OK, getByEmailResponse.getStatusCode());
        MemberDTO memberByEmail = getByEmailResponse.getBody();
        assertNotNull(memberByEmail);
        assertEquals(memberId, memberByEmail.getId());
        
        // 4. Verify the member appears in the list of all members
        ResponseEntity<List<MemberDTO>> getAllResponse = restTemplate.exchange(
                "/kitchensink/api/members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MemberDTO>>() {});
        
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        List<MemberDTO> allMembers = getAllResponse.getBody();
        assertNotNull(allMembers);
        
        boolean foundMember = allMembers.stream()
                .anyMatch(m -> m.getId().equals(memberId) && m.getEmail().equals(uniqueEmail));
        
        assertTrue(foundMember, "The newly created member should be in the list of all members");
        
        // 5. Attempt to create another member with the same email (should fail)
        CreateMemberDTO duplicateEmailMember = new CreateMemberDTO(
                "Another User",
                uniqueEmail, // Same email as before
                "1112223333");
        
        ResponseEntity<ErrorResponse> duplicateResponse = restTemplate.postForEntity(
                "/kitchensink/api/members",
                duplicateEmailMember,
                ErrorResponse.class);
        
        assertEquals(HttpStatus.CONFLICT, duplicateResponse.getStatusCode());
        ErrorResponse error = duplicateResponse.getBody();
        assertNotNull(error);
        assertEquals("CONFLICT", error.getCode());
    }
}
