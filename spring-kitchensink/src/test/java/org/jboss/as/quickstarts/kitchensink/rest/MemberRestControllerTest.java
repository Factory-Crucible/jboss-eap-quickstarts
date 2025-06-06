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
package org.jboss.as.quickstarts.kitchensink.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

/**
 * Unit tests for the MemberRestController using Spring MockMvc.
 * Tests the REST endpoints for listing all members, getting a member by ID, and creating a new member.
 */
@WebMvcTest(MemberRestController.class)
public class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Logger log;

    @MockBean
    private Validator validator;

    @MockBean
    private MemberRepository repository;

    @MockBean
    private MemberRegistration registration;

    private Member testMember1;
    private Member testMember2;
    private List<Member> allMembers;

    @BeforeEach
    public void setUp() {
        // Create test members
        testMember1 = new Member();
        testMember1.setId(1L);
        testMember1.setName("John Doe");
        testMember1.setEmail("john.doe@example.com");
        testMember1.setPhoneNumber("1234567890");

        testMember2 = new Member();
        testMember2.setId(2L);
        testMember2.setName("Jane Doe");
        testMember2.setEmail("jane.doe@example.com");
        testMember2.setPhoneNumber("9876543210");

        allMembers = Arrays.asList(testMember1, testMember2);
    }

    /**
     * Test listing all members
     */
    @Test
    public void testListAllMembers() throws Exception {
        // Set up mock behavior
        when(repository.findAllOrderedByName()).thenReturn(allMembers);

        // Perform GET request and verify response
        mockMvc.perform(get("/rest/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));
    }

    /**
     * Test getting a member by ID when the member exists
     */
    @Test
    public void testLookupMemberByIdFound() throws Exception {
        // Set up mock behavior
        when(repository.findById(1L)).thenReturn(Optional.of(testMember1));

        // Perform GET request and verify response
        mockMvc.perform(get("/rest/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    /**
     * Test getting a member by ID when the member does not exist
     */
    @Test
    public void testLookupMemberByIdNotFound() throws Exception {
        // Set up mock behavior
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Perform GET request and verify response
        mockMvc.perform(get("/rest/members/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test creating a new member successfully
     */
    @Test
    public void testCreateMemberSuccess() throws Exception {
        // Set up mock behavior
        when(validator.validate(any(Member.class))).thenReturn(new HashSet<>());
        when(repository.findByEmail(any(String.class))).thenReturn(null);
        doNothing().when(registration).register(any(Member.class));

        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("new.user@example.com");
        newMember.setPhoneNumber("5551234567");

        // Perform POST request and verify response
        mockMvc.perform(post("/rest/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isOk());
    }

    /**
     * Test creating a member with validation errors
     */
    @Test
    public void testCreateMemberValidationError() throws Exception {
        // Create a set of constraint violations
        Set<ConstraintViolation<Member>> violations = new HashSet<>();
        
        // Mock the validator behavior to throw a constraint violation exception
        when(validator.validate(any(Member.class))).thenThrow(new ConstraintViolationException("Validation failed", violations));

        Member invalidMember = new Member();
        invalidMember.setName(""); // Invalid name (too short)
        invalidMember.setEmail("invalid-email"); // Invalid email format
        invalidMember.setPhoneNumber("123"); // Invalid phone number (too short)

        // Perform POST request and verify response
        mockMvc.perform(post("/rest/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test creating a member with an email that already exists
     */
    @Test
    public void testCreateMemberDuplicateEmail() throws Exception {
        // Set up mock behavior
        when(validator.validate(any(Member.class))).thenReturn(new HashSet<>());
        when(repository.findByEmail("existing@example.com")).thenReturn(testMember1);

        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Another User");
        duplicateEmailMember.setEmail("existing@example.com"); // Email already exists
        duplicateEmailMember.setPhoneNumber("5559876543");

        // Perform POST request and verify response
        mockMvc.perform(post("/rest/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailMember)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.email", is("Email taken")));
    }

    /**
     * Test creating a member with a general exception during registration
     */
    @Test
    public void testCreateMemberGeneralError() throws Exception {
        // Set up mock behavior
        when(validator.validate(any(Member.class))).thenReturn(new HashSet<>());
        when(repository.findByEmail(any(String.class))).thenReturn(null);
        doThrow(new RuntimeException("Registration failed")).when(registration).register(any(Member.class));

        Member newMember = new Member();
        newMember.setName("Error User");
        newMember.setEmail("error.user@example.com");
        newMember.setPhoneNumber("5551234567");

        // Perform POST request and verify response
        mockMvc.perform(post("/rest/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Registration failed")));
    }
}
