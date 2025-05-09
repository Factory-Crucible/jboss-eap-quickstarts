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

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the MemberController.
 * Tests the REST endpoints for member management.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MemberControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    private static final String BASE_URL = "/api/members";

    @BeforeEach
    public void setup() {
        // Clear the repository before each test to ensure a clean state
        memberRepository.deleteAll();
    }

    @Test
    public void testCreateMember() {
        // Create a test member
        Member member = new Member();
        member.setName("Test User");
        member.setEmail("test@example.com");
        member.setPhoneNumber("1234567890");

        // Send POST request to create the member
        ResponseEntity<Member> response = restTemplate.postForEntity(
                BASE_URL, member, Member.class);

        // Verify the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test User");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().getPhoneNumber()).isEqualTo("1234567890");

        // Verify the member was saved in the repository
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    public void testGetAllMembers() {
        // Create some test members
        Member member1 = new Member();
        member1.setName("User One");
        member1.setEmail("user1@example.com");
        member1.setPhoneNumber("1111111111");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setName("User Two");
        member2.setEmail("user2@example.com");
        member2.setPhoneNumber("2222222222");
        memberRepository.save(member2);

        // Send GET request to retrieve all members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {});

        // Verify the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
        
        // Verify members are returned in alphabetical order by name
        assertThat(response.getBody().get(0).getName()).isEqualTo("User One");
        assertThat(response.getBody().get(1).getName()).isEqualTo("User Two");
    }

    @Test
    public void testGetMemberById() {
        // Create a test member
        Member member = new Member();
        member.setName("Test User");
        member.setEmail("test@example.com");
        member.setPhoneNumber("1234567890");
        Member savedMember = memberRepository.save(member);

        // Send GET request to retrieve the member by ID
        ResponseEntity<Member> response = restTemplate.getForEntity(
                BASE_URL + "/{id}", Member.class, savedMember.getId());

        // Verify the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedMember.getId());
        assertThat(response.getBody().getName()).isEqualTo("Test User");
    }

    @Test
    public void testCreateMemberWithInvalidData() {
        // Create a member with invalid data (missing required fields)
        Member member = new Member();
        // Not setting required fields

        // Send POST request to create the member
        ResponseEntity<Object> response = restTemplate.postForEntity(
                BASE_URL, member, Object.class);

        // Verify the response indicates a validation error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // Verify no member was saved
        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    public void testGetNonExistentMember() {
        // Send GET request for a non-existent member
        ResponseEntity<Object> response = restTemplate.getForEntity(
                BASE_URL + "/{id}", Object.class, 999L);

        // Verify the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
