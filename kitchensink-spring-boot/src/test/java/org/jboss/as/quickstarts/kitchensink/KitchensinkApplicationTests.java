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
package org.jboss.as.quickstarts.kitchensink;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Kitchensink Spring Boot application.
 * Tests all layers: REST endpoints, service, repository.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.properties")
class KitchensinkApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // Verify autowired components are not null
        assertNotNull(restTemplate);
        assertNotNull(memberRepository);
        assertNotNull(memberService);
    }

    // Repository Layer Tests

    @Test
    @DisplayName("Repository: Find member by ID")
    void testRepositoryFindById() {
        // Given: The sample member from data.sql
        // When: Finding by ID 1
        Optional<Member> foundMember = memberRepository.findById(1L);

        // Then: Member should be found
        assertTrue(foundMember.isPresent());
        assertEquals("John Smith", foundMember.get().getName());
        assertEquals("john.smith@mailinator.com", foundMember.get().getEmail());
    }

    @Test
    @DisplayName("Repository: Find member by email")
    void testRepositoryFindByEmail() {
        // Given: The sample member from data.sql
        // When: Finding by email
        Optional<Member> foundMember = memberRepository.findByEmail("john.smith@mailinator.com");

        // Then: Member should be found
        assertTrue(foundMember.isPresent());
        assertEquals("John Smith", foundMember.get().getName());
    }

    @Test
    @DisplayName("Repository: Find all members ordered by name")
    void testRepositoryFindAllOrderedByName() {
        // Given: At least one member in the database
        // When: Finding all members ordered by name
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Then: Should return non-empty list
        assertFalse(members.isEmpty());
        assertEquals("John Smith", members.get(0).getName());
    }

    // Service Layer Tests

    @Test
    @DisplayName("Service: Get all members")
    void testServiceGetAllMembers() {
        // When: Getting all members
        List<Member> members = memberService.getAllMembers();

        // Then: Should return non-empty list
        assertFalse(members.isEmpty());
        assertTrue(members.stream().anyMatch(m -> m.getEmail().equals("john.smith@mailinator.com")));
    }

    @Test
    @DisplayName("Service: Find member by ID")
    void testServiceFindMemberById() {
        // When: Finding by ID 1
        Optional<Member> foundMember = memberService.findMemberById(1L);

        // Then: Member should be found
        assertTrue(foundMember.isPresent());
        assertEquals("John Smith", foundMember.get().getName());
    }

    @Test
    @DisplayName("Service: Register new member successfully")
    void testServiceRegisterMember() throws Exception {
        // Given: A new member
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane.doe@example.com");
        newMember.setPhoneNumber("9876543210");

        // When: Registering the member
        Member savedMember = memberService.registerMember(newMember);

        // Then: Member should be saved with ID
        assertNotNull(savedMember.getId());
        assertEquals("Jane Doe", savedMember.getName());

        // Verify it's in the database
        assertTrue(memberRepository.findById(savedMember.getId()).isPresent());
    }

    @Test
    @DisplayName("Service: Duplicate email throws exception")
    void testServiceDuplicateEmail() {
        // Given: A member with an existing email
        Member duplicateMember = new Member();
        duplicateMember.setName("Duplicate User");
        duplicateMember.setEmail("john.smith@mailinator.com"); // Already exists
        duplicateMember.setPhoneNumber("5551234567");

        // When/Then: Registering should throw exception
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.registerMember(duplicateMember);
        });

        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    // REST API Tests

    @Test
    @DisplayName("REST: Get all members")
    void testRestGetAllMembers() {
        // When: GET request to /api/members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                "/api/members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {});

        // Then: Status should be 200 OK and body should contain members
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Member> members = response.getBody();
        assertNotNull(members);
        assertFalse(members.isEmpty());
        assertTrue(members.stream().anyMatch(m -> m.getEmail().equals("john.smith@mailinator.com")));
    }

    @Test
    @DisplayName("REST: Get member by ID")
    void testRestGetMemberById() {
        // When: GET request to /api/members/1
        ResponseEntity<Member> response = restTemplate.getForEntity("/api/members/1", Member.class);

        // Then: Status should be 200 OK and body should contain the member
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Member member = response.getBody();
        assertNotNull(member);
        assertEquals("John Smith", member.getName());
        assertEquals("john.smith@mailinator.com", member.getEmail());
    }

    @Test
    @DisplayName("REST: Get non-existent member returns 404")
    void testRestGetNonExistentMember() {
        // When: GET request to /api/members/999 (non-existent ID)
        ResponseEntity<Member> response = restTemplate.getForEntity("/api/members/999", Member.class);

        // Then: Status should be 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("REST: Create new member successfully")
    void testRestCreateMember() {
        // Given: A new member
        Member newMember = new Member();
        newMember.setName("Robert Johnson");
        newMember.setEmail("robert.johnson@example.com");
        newMember.setPhoneNumber("5559876543");

        // When: POST request to /api/members
        ResponseEntity<Member> response = restTemplate.postForEntity("/api/members", newMember, Member.class);

        // Then: Status should be 200 OK and body should contain the created member with ID
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Member createdMember = response.getBody();
        assertNotNull(createdMember);
        assertNotNull(createdMember.getId());
        assertEquals("Robert Johnson", createdMember.getName());
        assertEquals("robert.johnson@example.com", createdMember.getEmail());

        // Verify it's in the database
        assertTrue(memberRepository.findById(createdMember.getId()).isPresent());
    }

    @Test
    @DisplayName("REST: Create member with validation errors returns 400")
    void testRestCreateMemberValidationErrors() {
        // Given: An invalid member (missing required fields)
        Member invalidMember = new Member();
        invalidMember.setName(""); // Invalid: empty name
        invalidMember.setEmail("invalid-email"); // Invalid: not an email format
        invalidMember.setPhoneNumber("123"); // Invalid: too short

        // When: POST request to /api/members
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/members",
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {});

        // Then: Status should be 400 Bad Request and body should contain validation errors
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        // Should contain validation errors for name, email and phoneNumber
        assertTrue(errors.containsKey("name") || errors.containsKey("email") || errors.containsKey("phoneNumber"));
    }

    @Test
    @DisplayName("REST: Create member with duplicate email returns 409")
    void testRestCreateMemberDuplicateEmail() {
        // Given: A member with an existing email
        Member duplicateMember = new Member();
        duplicateMember.setName("Another John");
        duplicateMember.setEmail("john.smith@mailinator.com"); // Already exists
        duplicateMember.setPhoneNumber("5551234567");

        // When: POST request to /api/members
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/members",
                HttpMethod.POST,
                new HttpEntity<>(duplicateMember),
                new ParameterizedTypeReference<Map<String, String>>() {});

        // Then: Status should be 409 Conflict and body should contain error message
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("email"));
        assertEquals("Email taken", errors.get("email"));
    }
}
