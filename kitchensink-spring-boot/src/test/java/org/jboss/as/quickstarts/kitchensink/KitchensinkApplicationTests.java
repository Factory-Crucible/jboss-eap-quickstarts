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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Kitchensink Spring Boot application.
 * Tests all components including REST API, validation, service layer, and database operations.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
class KitchensinkApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/members";
    }

    @Test
    @DisplayName("Test 1: Spring context loads successfully")
    void contextLoads() {
        assertThat(memberRepository).isNotNull();
        assertThat(memberService).isNotNull();
    }

    @Test
    @DisplayName("Test 2: Sample data is loaded correctly on startup")
    void sampleDataLoaded() {
        List<Member> members = memberRepository.findAll();
        assertThat(members).isNotEmpty();
        
        Optional<Member> johnSmith = memberRepository.findByEmail("john.smith@mailinator.com");
        assertTrue(johnSmith.isPresent());
        assertEquals("John Smith", johnSmith.get().getName());
        assertEquals("2125551212", johnSmith.get().getPhoneNumber());
    }

    @Test
    @DisplayName("Test 3: GET /api/members returns all members")
    void getAllMembers() {
        ResponseEntity<List<Member>> response = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Member>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Verify sample data is included
        boolean foundJohnSmith = response.getBody().stream()
            .anyMatch(m -> "john.smith@mailinator.com".equals(m.getEmail()));
        assertTrue(foundJohnSmith, "Sample data member should be present");
    }

    @Test
    @DisplayName("Test 4: GET /api/members/{id} returns correct member")
    void getMemberById() {
        // First, get all members to find a valid ID
        ResponseEntity<List<Member>> allMembersResponse = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Member>>() {}
        );
        
        assertFalse(allMembersResponse.getBody().isEmpty());
        Long validId = allMembersResponse.getBody().get(0).getId();
        
        // Now get the specific member
        ResponseEntity<Member> response = restTemplate.getForEntity(
            getBaseUrl() + "/" + validId,
            Member.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validId, response.getBody().getId());
    }

    @Test
    @DisplayName("Test 5: GET /api/members/{id} with invalid ID returns 404")
    void getMemberByInvalidId() {
        ResponseEntity<Member> response = restTemplate.getForEntity(
            getBaseUrl() + "/999999",
            Member.class
        );
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Test 6: POST /api/members creates new member")
    void createMember() {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane.doe@example.com");
        newMember.setPhoneNumber("2025559876");
        
        ResponseEntity<Member> response = restTemplate.postForEntity(
            getBaseUrl(),
            newMember,
            Member.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Jane Doe", response.getBody().getName());
        assertEquals("jane.doe@example.com", response.getBody().getEmail());
        
        // Verify it was actually saved to the database
        Optional<Member> savedMember = memberRepository.findByEmail("jane.doe@example.com");
        assertTrue(savedMember.isPresent());
    }

    @Test
    @DisplayName("Test 7: POST /api/members with duplicate email returns 409 CONFLICT")
    void createMemberWithDuplicateEmail() {
        // Create a member with the same email as the sample data
        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Another John");
        duplicateEmailMember.setEmail("john.smith@mailinator.com"); // Same as sample data
        duplicateEmailMember.setPhoneNumber("2025551234");
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            new HttpEntity<>(duplicateEmailMember),
            new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("email"));
    }

    @Test
    @DisplayName("Test 8: POST /api/members with invalid name (contains numbers) fails validation")
    void createMemberWithInvalidName() {
        Member invalidMember = new Member();
        invalidMember.setName("Jane123"); // Contains numbers, violates pattern
        invalidMember.setEmail("jane123@example.com");
        invalidMember.setPhoneNumber("2025559876");
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            new HttpEntity<>(invalidMember),
            new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("name"));
    }

    @Test
    @DisplayName("Test 9: POST /api/members with invalid email fails validation")
    void createMemberWithInvalidEmail() {
        Member invalidMember = new Member();
        invalidMember.setName("Jane Doe");
        invalidMember.setEmail("not-an-email"); // Invalid email format
        invalidMember.setPhoneNumber("2025559876");
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            new HttpEntity<>(invalidMember),
            new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("email"));
    }

    @Test
    @DisplayName("Test 10: POST /api/members with invalid phone number (too short) fails validation")
    void createMemberWithInvalidPhoneNumberTooShort() {
        Member invalidMember = new Member();
        invalidMember.setName("Jane Doe");
        invalidMember.setEmail("jane.doe@example.com");
        invalidMember.setPhoneNumber("123456"); // Too short
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            new HttpEntity<>(invalidMember),
            new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("phoneNumber"));
    }

    @Test
    @DisplayName("Test 11: POST /api/members with invalid phone number (non-digits) fails validation")
    void createMemberWithInvalidPhoneNumberNonDigits() {
        Member invalidMember = new Member();
        invalidMember.setName("Jane Doe");
        invalidMember.setEmail("jane.doe@example.com");
        invalidMember.setPhoneNumber("202-555-ABCD"); // Contains non-digits
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.POST,
            new HttpEntity<>(invalidMember),
            new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("phoneNumber"));
    }

    @Test
    @DisplayName("Test 12: Service layer correctly registers a new member")
    @Transactional
    void memberServiceRegistersNewMember() throws Exception {
        Member newMember = new Member();
        newMember.setName("Service Test");
        newMember.setEmail("service.test@example.com");
        newMember.setPhoneNumber("2025550000");
        
        Member savedMember = memberService.register(newMember);
        
        assertNotNull(savedMember);
        assertNotNull(savedMember.getId());
        
        // Verify it was saved in the database
        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
        assertTrue(foundMember.isPresent());
        assertEquals("Service Test", foundMember.get().getName());
    }

    @Test
    @DisplayName("Test 13: Repository correctly finds members ordered by name")
    void repositoryFindsAllOrderedByName() {
        // Add a few members with different names to ensure ordering
        try {
            Member memberA = new Member();
            memberA.setName("Aaron Smith");
            memberA.setEmail("aaron@example.com");
            memberA.setPhoneNumber("2025551111");
            memberService.register(memberA);
            
            Member memberZ = new Member();
            memberZ.setName("Zack Brown");
            memberZ.setEmail("zack@example.com");
            memberZ.setPhoneNumber("2025552222");
            memberService.register(memberZ);
        } catch (Exception e) {
            fail("Failed to set up test data: " + e.getMessage());
        }
        
        List<Member> members = memberRepository.findAllByOrderByNameAsc();
        
        assertFalse(members.isEmpty());
        
        // Verify the ordering - names should be in alphabetical order
        for (int i = 0; i < members.size() - 1; i++) {
            String currentName = members.get(i).getName().toLowerCase();
            String nextName = members.get(i + 1).getName().toLowerCase();
            assertTrue(currentName.compareTo(nextName) <= 0, 
                "Members should be ordered by name: " + currentName + " should come before " + nextName);
        }
    }

    @Test
    @DisplayName("Test 14: Service layer handles duplicate email correctly")
    void memberServiceHandlesDuplicateEmail() {
        Member duplicateMember = new Member();
        duplicateMember.setName("Duplicate Test");
        duplicateMember.setEmail("john.smith@mailinator.com"); // Same as sample data
        duplicateMember.setPhoneNumber("2025553333");
        
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.register(duplicateMember);
        });
        
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    @DisplayName("Test 15: Repository correctly finds member by email")
    void repositoryFindsByEmail() {
        Optional<Member> member = memberRepository.findByEmail("john.smith@mailinator.com");
        
        assertTrue(member.isPresent());
        assertEquals("John Smith", member.get().getName());
        assertEquals("2125551212", member.get().getPhoneNumber());
    }
}
