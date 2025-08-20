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
package org.jboss.as.quickstarts.kitchensink.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the MemberController REST endpoints.
 * 
 * These tests verify the behavior of the REST API by making actual HTTP requests
 * to a running instance of the application with a test database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Transactional
@Rollback
public class MemberControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    private String baseUrl;
    private Member johnSmith; // Initial member from data.sql

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/members";
        
        // Find the initial John Smith member from data.sql
        johnSmith = memberRepository.findByEmail("john.smith@mailinator.com");
        assertNotNull(johnSmith, "Initial test data member should exist");
    }

    /**
     * Test listing all members.
     * Should include the initial John Smith member from data.sql.
     */
    @Test
    public void testListAllMembers() {
        ResponseEntity<List<Member>> response = restTemplate.exchange(
            baseUrl, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Member>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Member> members = response.getBody();
        assertNotNull(members);
        assertThat(members).isNotEmpty();
        
        // Verify John Smith is in the list
        boolean foundJohnSmith = members.stream()
            .anyMatch(m -> "john.smith@mailinator.com".equals(m.getEmail()));
        assertTrue(foundJohnSmith, "John Smith should be in the member list");
    }

    /**
     * Test finding a member by ID.
     * Should return the member with the specified ID.
     */
    @Test
    public void testFindMemberById() {
        ResponseEntity<Member> response = restTemplate.getForEntity(
            baseUrl + "/" + johnSmith.getId(), 
            Member.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Member member = response.getBody();
        assertNotNull(member);
        assertEquals(johnSmith.getId(), member.getId());
        assertEquals(johnSmith.getName(), member.getName());
        assertEquals(johnSmith.getEmail(), member.getEmail());
        assertEquals(johnSmith.getPhoneNumber(), member.getPhoneNumber());
    }

    /**
     * Test finding a non-existent member by ID.
     * Should return a 404 Not Found status.
     */
    @Test
    public void testFindNonExistentMemberById() {
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            baseUrl + "/999", 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertTrue(error.containsKey("error"));
    }

    /**
     * Test creating a valid member.
     * Should return a 200 OK status.
     */
    @Test
    public void testCreateValidMember() {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane.doe@example.com");
        newMember.setPhoneNumber("2025551234");

        ResponseEntity<Void> response = restTemplate.postForEntity(
            baseUrl, 
            newMember, 
            Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify the member was created in the database
        Member savedMember = memberRepository.findByEmail("jane.doe@example.com");
        assertNotNull(savedMember);
        assertEquals("Jane Doe", savedMember.getName());
        assertEquals("2025551234", savedMember.getPhoneNumber());
    }

    /**
     * Test creating a member with validation errors.
     * Should return a 400 Bad Request status with validation error details.
     */
    @Test
    public void testCreateMemberWithValidationErrors() {
        // Create member with invalid data
        Member invalidMember = new Member();
        invalidMember.setName("Jane123"); // Contains numbers, violating pattern
        invalidMember.setEmail("not-an-email"); // Invalid email
        invalidMember.setPhoneNumber("123"); // Too short

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            baseUrl, 
            HttpMethod.POST, 
            new HttpEntity<>(invalidMember), 
            new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        
        // Check for specific validation errors
        assertTrue(errors.containsKey("name"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("phoneNumber"));
    }

    /**
     * Test creating a member with a duplicate email.
     * Should return a 409 Conflict status.
     */
    @Test
    public void testCreateMemberWithDuplicateEmail() {
        // Create member with same email as John Smith
        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Another John");
        duplicateEmailMember.setEmail("john.smith@mailinator.com"); // Same as existing
        duplicateEmailMember.setPhoneNumber("3035551234");

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            baseUrl, 
            HttpMethod.POST, 
            new HttpEntity<>(duplicateEmailMember), 
            new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertTrue(error.containsKey("email"));
        assertEquals("Email taken", error.get("email"));
    }

    /**
     * Test creating a member with missing required fields.
     * Should return a 400 Bad Request status with validation error details.
     */
    @Test
    public void testCreateMemberWithMissingRequiredFields() {
        // Create member with null values
        Member emptyMember = new Member();

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            baseUrl, 
            HttpMethod.POST, 
            new HttpEntity<>(emptyMember), 
            new ParameterizedTypeReference<Map<String, String>>() {}
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        
        // Check for required field validation errors
        assertTrue(errors.containsKey("name"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("phoneNumber"));
    }
}
