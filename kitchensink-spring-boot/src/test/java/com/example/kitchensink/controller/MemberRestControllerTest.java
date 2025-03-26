package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link MemberRestController}.
 * These tests verify that the REST endpoints work as expected.
 */
@WebMvcTest(MemberRestController.class)
public class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private Member john;
    private Member jane;
    private Member bob;

    @BeforeEach
    public void setUp() {
        // Create test members
        john = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@mailinator.com")
                .phoneNumber("2125551212")
                .build();

        jane = Member.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane.doe@mailinator.com")
                .phoneNumber("2125552323")
                .build();

        bob = Member.builder()
                .id(3L)
                .name("Bob Johnson")
                .email("bob.johnson@mailinator.com")
                .phoneNumber("2125553434")
                .build();
    }

    @Test
    public void testGetAllMembers() throws Exception {
        // Given
        List<Member> members = Arrays.asList(john, jane, bob);
        when(memberService.findAllMembersOrderedByName()).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Smith")))
                .andExpect(jsonPath("$[0].email", is("john.smith@mailinator.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is("2125551212")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Bob Johnson")));

        verify(memberService, times(1)).findAllMembersOrderedByName();
    }

    @Test
    public void testGetMember() throws Exception {
        // Given
        when(memberService.findMemberById(1L)).thenReturn(Optional.of(john));

        // When & Then
        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Smith")))
                .andExpect(jsonPath("$.email", is("john.smith@mailinator.com")))
                .andExpect(jsonPath("$.phoneNumber", is("2125551212")));

        verify(memberService, times(1)).findMemberById(1L);
    }

    @Test
    public void testGetMember_NotFound() throws Exception {
        // Given
        when(memberService.findMemberById(99L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/members/99"))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).findMemberById(99L);
    }

    @Test
    public void testCreateMember() throws Exception {
        // Given
        Member newMember = Member.builder()
                .name("Alice Williams")
                .email("alice.williams@mailinator.com")
                .phoneNumber("2125554545")
                .build();

        Member savedMember = Member.builder()
                .id(4L)
                .name("Alice Williams")
                .email("alice.williams@mailinator.com")
                .phoneNumber("2125554545")
                .build();

        when(memberService.createMember(any(Member.class))).thenReturn(savedMember);

        // When & Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("Alice Williams")))
                .andExpect(jsonPath("$.email", is("alice.williams@mailinator.com")))
                .andExpect(jsonPath("$.phoneNumber", is("2125554545")));

        verify(memberService, times(1)).createMember(any(Member.class));
    }

    @Test
    public void testCreateMember_ValidationError() throws Exception {
        // Given
        Member invalidMember = Member.builder()
                .name("") // Invalid: empty name
                .email("invalid-email") // Invalid: not an email
                .phoneNumber("123") // Invalid: too short
                .build();

        // When & Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).createMember(any(Member.class));
    }

    @Test
    public void testCreateMember_EmailAlreadyExists() throws Exception {
        // Given
        Member newMember = Member.builder()
                .name("John Clone")
                .email("john.smith@mailinator.com") // Email already exists
                .phoneNumber("2125556767")
                .build();

        when(memberService.createMember(any(Member.class))).thenThrow(new IllegalArgumentException("Email already exists: john.smith@mailinator.com"));

        // When & Then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Email already exists: john.smith@mailinator.com")));

        verify(memberService, times(1)).createMember(any(Member.class));
    }

    @Test
    public void testUpdateMember() throws Exception {
        // Given
        Member updatedMember = Member.builder()
                .name("John Smith Updated")
                .email("john.updated@mailinator.com")
                .phoneNumber("2125559999")
                .build();

        Member savedMember = Member.builder()
                .id(1L)
                .name("John Smith Updated")
                .email("john.updated@mailinator.com")
                .phoneNumber("2125559999")
                .build();

        when(memberService.updateMember(eq(1L), any(Member.class))).thenReturn(savedMember);

        // When & Then
        mockMvc.perform(put("/api/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Smith Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@mailinator.com")))
                .andExpect(jsonPath("$.phoneNumber", is("2125559999")));

        verify(memberService, times(1)).updateMember(eq(1L), any(Member.class));
    }

    @Test
    public void testUpdateMember_NotFound() throws Exception {
        // Given
        Member updatedMember = Member.builder()
                .name("Nonexistent User")
                .email("nonexistent@mailinator.com")
                .phoneNumber("2125551111")
                .build();

        when(memberService.updateMember(eq(99L), any(Member.class))).thenThrow(new IllegalArgumentException("Member not found with ID: 99"));

        // When & Then
        mockMvc.perform(put("/api/members/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Member not found with ID: 99")));

        verify(memberService, times(1)).updateMember(eq(99L), any(Member.class));
    }

    @Test
    public void testUpdateMember_ValidationError() throws Exception {
        // Given
        Member invalidMember = Member.builder()
                .name("") // Invalid: empty name
                .email("invalid-email") // Invalid: not an email
                .phoneNumber("123") // Invalid: too short
                .build();

        // When & Then
        mockMvc.perform(put("/api/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).updateMember(anyLong(), any(Member.class));
    }

    @Test
    public void testDeleteMember() throws Exception {
        // Given
        doNothing().when(memberService).deleteMember(1L);

        // When & Then
        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).deleteMember(1L);
    }

    @Test
    public void testDeleteMember_NotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Member not found with ID: 99")).when(memberService).deleteMember(99L);

        // When & Then
        mockMvc.perform(delete("/api/members/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Member not found with ID: 99")));

        verify(memberService, times(1)).deleteMember(99L);
    }
}
