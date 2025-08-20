/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.springboot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.jboss.as.quickstarts.kitchensink.springboot.model.Member;
import org.jboss.as.quickstarts.kitchensink.springboot.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.springboot.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for the Member functionality.
 * Tests the complete stack from REST API to database persistence.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class MemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    public void setup() {
        // Clear any test data
        memberRepository.deleteAll();

        // Create a test member
        testMember = new Member();
        testMember.setName("Test User");
        testMember.setEmail("test@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Context loads successfully")
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(restTemplate).isNotNull();
        assertThat(memberRepository).isNotNull();
        assertThat(memberService).isNotNull();
    }

    // Repository Tests
    @Test
    @DisplayName("Repository: Find member by email")
    public void testFindByEmail() {
        // Save test member
        memberRepository.save(testMember);

        // Find by email
        assertThat(memberRepository.findByEmail("test@example.com"))
            .isPresent()
            .get()
            .extracting(Member::getName)
            .isEqualTo("Test User");
    }

    @Test
    @DisplayName("Repository: Find all members ordered by name")
    public void testFindAllOrderedByName() {
        // Create and save multiple members
        Member member1 = new Member(null, "Charlie", "charlie@example.com", "1234567890");
        Member member2 = new Member(null, "Alice", "alice@example.com", "1234567890");
        Member member3 = new Member(null, "Bob", "bob@example.com", "1234567890");

        memberRepository.saveAll(List.of(member1, member2, member3));

        // Retrieve ordered list
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Verify order
        assertThat(members).hasSize(3);
        assertThat(members.get(0).getName()).isEqualTo("Alice");
        assertThat(members.get(1).getName()).isEqualTo("Bob");
        assertThat(members.get(2).getName()).isEqualTo("Charlie");
    }

    // Service Tests
    @Test
    @DisplayName("Service: Register new member")
    public void testRegisterMember() {
        // Register member via service
        Member savedMember = memberService.register(testMember);

        // Verify member was saved
        assertThat(savedMember.getId()).isNotNull();
        assertThat(memberRepository.findById(savedMember.getId())).isPresent();
    }

    @Test
    @DisplayName("Service: Duplicate email throws exception")
    public void testDuplicateEmailThrowsException() {
        // Save first member
        memberService.register(testMember);

        // Create second member with same email
        Member duplicateMember = new Member();
        duplicateMember.setName("Another User");
        duplicateMember.setEmail("test@example.com");
        duplicateMember.setPhoneNumber("9876543210");

        // Verify exception is thrown
        try {
            memberService.register(duplicateMember);
            assertThat(false).isTrue(); // Should not reach here
        } catch (MemberService.DuplicateEmailException e) {
            assertThat(e.getMessage()).contains("Email already exists");
        }
    }

    // REST API Tests with MockMvc
    @Test
    @DisplayName("REST API: Get all members")
    public void testGetAllMembers() throws Exception {
        // Save test member
        memberRepository.save(testMember);

        // Test GET /api/members
        mockMvc.perform(get("/api/members")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].name").value("Test User"))
            .andExpect(jsonPath("$[0].email").value("test@example.com"))
            .andExpect(jsonPath("$[0].phoneNumber").value("1234567890"));
    }

    @Test
    @DisplayName("REST API: Get member by ID")
    public void testGetMemberById() throws Exception {
        // Save test member
        Member savedMember = memberRepository.save(testMember);

        // Test GET /api/members/{id}
        mockMvc.perform(get("/api/members/" + savedMember.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(savedMember.getId()))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    @DisplayName("REST API: Get non-existent member returns 404")
    public void testGetNonExistentMember() throws Exception {
        // Test GET /api/members/{non-existent-id}
        mockMvc.perform(get("/api/members/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("REST API: Create member")
    public void testCreateMember() throws Exception {
        // Test POST /api/members
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(notNullValue())))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.phoneNumber").value("1234567890"));

        // Verify member was saved to database
        assertThat(memberRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("REST API: Create member with invalid email")
    public void testCreateMemberWithInvalidEmail() throws Exception {
        // Create member with invalid email
        testMember.setEmail("invalid-email");

        // Test POST /api/members with invalid data
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("REST API: Create member with missing name")
    public void testCreateMemberWithMissingName() throws Exception {
        // Create member with missing name
        testMember.setName(null);

        // Test POST /api/members with invalid data
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("REST API: Create member with invalid phone")
    public void testCreateMemberWithInvalidPhone() throws Exception {
        // Create member with invalid phone (too short)
        testMember.setPhoneNumber("123");

        // Test POST /api/members with invalid data
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    @DisplayName("REST API: Create member with name containing numbers")
    public void testCreateMemberWithNameContainingNumbers() throws Exception {
        // Create member with name containing numbers
        testMember.setName("User123");

        // Test POST /api/members with invalid data
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("REST API: Create member with duplicate email")
    public void testCreateMemberWithDuplicateEmail() throws Exception {
        // Save first member
        memberRepository.save(testMember);

        // Create second member with same email
        Member duplicateMember = new Member();
        duplicateMember.setName("Another User");
        duplicateMember.setEmail("test@example.com");
        duplicateMember.setPhoneNumber("9876543210");

        // Test POST /api/members with duplicate email
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateMember))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors.email").value("Email taken"));
    }

    // REST API Tests with TestRestTemplate
    @Test
    @DisplayName("REST API (TestRestTemplate): Get all members")
    public void testGetAllMembersWithRestTemplate() {
        // Save test member
        memberRepository.save(testMember);

        // Test GET /api/members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
            "/api/members",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Member>>() {}
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Test User");
        assertThat(response.getBody().get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("REST API (TestRestTemplate): Create member")
    public void testCreateMemberWithRestTemplate() {
        // Test POST /api/members
        ResponseEntity<Member> response = restTemplate.postForEntity(
            "/api/members",
            testMember,
            Member.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test User");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");

        // Verify member was saved to database
        assertThat(memberRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("REST API (TestRestTemplate): Create member with validation errors")
    public void testCreateMemberWithValidationErrorsUsingRestTemplate() {
        // Create member with invalid data
        testMember.setEmail("invalid-email");
        testMember.setPhoneNumber("123");

        // Test POST /api/members with invalid data
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            "/api/members",
            HttpMethod.POST,
            new HttpEntity<>(testMember),
            new ParameterizedTypeReference<>() {}
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("email");
        assertThat(response.getBody()).containsKey("phoneNumber");
    }
}
