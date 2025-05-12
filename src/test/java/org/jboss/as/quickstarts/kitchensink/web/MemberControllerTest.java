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
package org.jboss.as.quickstarts.kitchensink.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the {@link MemberController} REST endpoints.
 * Uses Spring's MockMvc to test the controller without starting a full HTTP server.
 */
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private MemberRegistration memberRegistration;

    @MockBean
    private Logger logger;

    /**
     * Tests that the endpoint to list all members returns the expected members in the correct order.
     */
    @Test
    public void testListAllMembers() throws Exception {
        // Given
        Member member1 = createMember(1L, "John Doe", "john@example.com", "1234567890");
        Member member2 = createMember(2L, "Jane Doe", "jane@example.com", "0987654321");
        List<Member> members = Arrays.asList(member1, member2);
        
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(members);

        // When/Then
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is("1234567890")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")))
                .andExpect(jsonPath("$[1].phoneNumber", is("0987654321")));
        
        verify(memberRepository, times(1)).findAllByOrderByNameAsc();
    }

    /**
     * Tests that the endpoint to list all members returns an empty array when no members exist.
     */
    @Test
    public void testListAllMembersEmpty() throws Exception {
        // Given
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(List.of());

        // When/Then
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(memberRepository, times(1)).findAllByOrderByNameAsc();
    }

    /**
     * Tests that the endpoint to look up a member by ID returns the expected member when found.
     */
    @Test
    public void testLookupMemberByIdFound() throws Exception {
        // Given
        Member member = createMember(1L, "John Doe", "john@example.com", "1234567890");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // When/Then
        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
        
        verify(memberRepository, times(1)).findById(1L);
    }

    /**
     * Tests that the endpoint to look up a member by ID returns 404 Not Found when the member doesn't exist.
     */
    @Test
    public void testLookupMemberByIdNotFound() throws Exception {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound());
        
        verify(memberRepository, times(1)).findById(999L);
    }

    /**
     * Tests that the endpoint to create a member returns 200 OK when the member is created successfully.
     */
    @Test
    public void testCreateMemberSuccess() throws Exception {
        // Given
        Member member = createMember(null, "John Doe", "john@example.com", "1234567890");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        doNothing().when(memberRegistration).register(any(Member.class));

        // When/Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());
        
        verify(memberRepository, times(1)).findByEmail("john@example.com");
        verify(memberRegistration, times(1)).register(any(Member.class));
    }

    /**
     * Tests that the endpoint to create a member returns 409 Conflict when a member with the same email already exists.
     */
    @Test
    public void testCreateMemberDuplicateEmail() throws Exception {
        // Given
        Member existingMember = createMember(1L, "John Doe", "john@example.com", "1234567890");
        Member newMember = createMember(null, "John Smith", "john@example.com", "0987654321");
        
        when(memberRepository.findByEmail("john@example.com")).thenReturn(Optional.of(existingMember));

        // When/Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.email", is("Email taken")));
        
        verify(memberRepository, times(1)).findByEmail("john@example.com");
        verify(memberRegistration, never()).register(any(Member.class));
    }

    /**
     * Tests that the endpoint to create a member returns 400 Bad Request when validation fails.
     */
    @Test
    public void testCreateMemberValidationFailure() throws Exception {
        // Given
        Member invalidMember = new Member();
        invalidMember.setName("John123"); // Contains numbers, which is invalid
        invalidMember.setEmail("not-an-email"); // Invalid email format
        invalidMember.setPhoneNumber("123"); // Too short

        // When/Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", containsString("Must not contain numbers")))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.phoneNumber", notNullValue()));
        
        verify(memberRepository, never()).findByEmail(anyString());
        verify(memberRegistration, never()).register(any(Member.class));
    }

    /**
     * Tests that the endpoint to create a member returns 400 Bad Request when an exception occurs during registration.
     */
    @Test
    public void testCreateMemberException() throws Exception {
        // Given
        Member member = createMember(null, "John Doe", "john@example.com", "1234567890");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Registration failed")).when(memberRegistration).register(any(Member.class));

        // When/Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Registration failed")));
        
        verify(memberRepository, times(1)).findByEmail("john@example.com");
        verify(memberRegistration, times(1)).register(any(Member.class));
    }

    /**
     * Helper method to create a Member object with the given properties.
     */
    private Member createMember(Long id, String name, String email, String phoneNumber) {
        Member member = new Member();
        member.setId(id);
        member.setName(name);
        member.setEmail(email);
        member.setPhoneNumber(phoneNumber);
        return member;
    }
}
