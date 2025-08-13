package com.example.kitchensink.integration;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link com.example.kitchensink.controller.MemberController} REST controller.
 * Tests all endpoints and validation scenarios using MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private static final String API_BASE_PATH = "/api/members";

    @BeforeEach
    void setUp() {
        // Create a valid test member
        testMember = new Member();
        testMember.setName("Test User");
        testMember.setEmail("test.user@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/members - Should return all members")
    void shouldReturnAllMembers() throws Exception {
        // Given: data.sql has loaded sample members

        // When: GET request to /api/members
        ResultActions response = mockMvc.perform(get(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        // Then: Status should be 200 OK and response should contain all members
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                // Verify we have at least the sample data from data.sql
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/members/{id} - Should return member when found")
    void shouldReturnMemberById() throws Exception {
        // Given: A member exists in the database
        List<Member> members = memberRepository.findAll();
        Member existingMember = members.get(0);

        // When: GET request to /api/members/{id}
        ResultActions response = mockMvc.perform(get(API_BASE_PATH + "/{id}", existingMember.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then: Status should be 200 OK and response should contain the member
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(existingMember.getId().intValue())))
                .andExpect(jsonPath("$.name", is(existingMember.getName())))
                .andExpect(jsonPath("$.email", is(existingMember.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(existingMember.getPhoneNumber())));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/members/{id} - Should return 404 when member not found")
    void shouldReturn404WhenMemberNotFound() throws Exception {
        // Given: A non-existent member ID
        long nonExistentId = 999L;

        // When: GET request to /api/members/{id} with non-existent ID
        ResultActions response = mockMvc.perform(get(API_BASE_PATH + "/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then: Status should be 404 Not Found
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/members - Should create new member")
    void shouldCreateNewMember() throws Exception {
        // Given: A valid member object
        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("new.user@example.com");
        newMember.setPhoneNumber("9876543210");

        // When: POST request to /api/members with valid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)));

        // Then: Status should be 201 Created and response should contain the created member
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(newMember.getName())))
                .andExpect(jsonPath("$.email", is(newMember.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(newMember.getPhoneNumber())));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/members - Should return 400 when name is missing")
    void shouldReturn400WhenNameIsMissing() throws Exception {
        // Given: A member with missing name
        Member invalidMember = new Member();
        invalidMember.setEmail("invalid@example.com");
        invalidMember.setPhoneNumber("1234567890");

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain validation error
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/members - Should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given: A member with invalid email
        Member invalidMember = new Member();
        invalidMember.setName("Invalid Email User");
        invalidMember.setEmail("invalid-email");  // Invalid email format
        invalidMember.setPhoneNumber("1234567890");

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain validation error
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/members - Should return 400 when phone number is invalid")
    void shouldReturn400WhenPhoneNumberIsInvalid() throws Exception {
        // Given: A member with invalid phone number
        Member invalidMember = new Member();
        invalidMember.setName("Invalid Phone User");
        invalidMember.setEmail("invalid.phone@example.com");
        invalidMember.setPhoneNumber("123");  // Too short

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain validation error
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/members - Should return 400 when name contains numbers")
    void shouldReturn400WhenNameContainsNumbers() throws Exception {
        // Given: A member with name containing numbers
        Member invalidMember = new Member();
        invalidMember.setName("User123");  // Contains numbers
        invalidMember.setEmail("user123@example.com");
        invalidMember.setPhoneNumber("1234567890");

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain validation error
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name", is("Must not contain numbers")));
    }

    @Test
    @Order(9)
    @DisplayName("POST /api/members - Should return 409 when email already exists")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        // Given: A member with duplicate email
        // First, ensure we have a member with this email
        Member existingMember = memberRepository.findAll().get(0);
        
        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Duplicate Email User");
        duplicateEmailMember.setEmail(existingMember.getEmail());  // Use existing email
        duplicateEmailMember.setPhoneNumber("9876543210");

        // When: POST request to /api/members with duplicate email
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailMember)));

        // Then: Status should be 409 Conflict and response should contain error
        response.andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("Email taken")));
    }

    @Test
    @Order(10)
    @DisplayName("POST /api/members - Should validate multiple fields")
    void shouldValidateMultipleFields() throws Exception {
        // Given: A member with multiple validation errors
        Member invalidMember = new Member();
        // All fields are empty or null

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain multiple validation errors
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    @Order(11)
    @DisplayName("POST /api/members - Should validate phone number digits only")
    void shouldValidatePhoneNumberDigitsOnly() throws Exception {
        // Given: A member with non-digit characters in phone number
        Member invalidMember = new Member();
        invalidMember.setName("Phone Digits User");
        invalidMember.setEmail("phone.digits@example.com");
        invalidMember.setPhoneNumber("123-456-7890");  // Contains non-digit characters

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain validation error
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    @Order(12)
    @DisplayName("POST /api/members - Should validate name length")
    void shouldValidateNameLength() throws Exception {
        // Given: A member with too long name
        Member invalidMember = new Member();
        invalidMember.setName("This name is way too long and exceeds the maximum allowed length of twenty five characters");
        invalidMember.setEmail("long.name@example.com");
        invalidMember.setPhoneNumber("1234567890");

        // When: POST request to /api/members with invalid member
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)));

        // Then: Status should be 400 Bad Request and response should contain validation error
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").exists());
    }
}
