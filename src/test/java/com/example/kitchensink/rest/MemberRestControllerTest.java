package com.example.kitchensink.rest;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ValidationException;

/**
 * Unit tests for the MemberRestController.
 * These tests verify the REST API endpoints using MockMvc.
 */
@WebMvcTest(MemberRestController.class)
public class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private MemberRegistrationService memberRegistrationService;

    private Member testMember1;
    private Member testMember2;

    @BeforeEach
    public void setup() {
        // Create test members
        testMember1 = new Member();
        testMember1.setId(1L);
        testMember1.setName("John Doe");
        testMember1.setEmail("john.doe@example.com");
        testMember1.setPhoneNumber("1234567890");

        testMember2 = new Member();
        testMember2.setId(2L);
        testMember2.setName("Jane Smith");
        testMember2.setEmail("jane.smith@example.com");
        testMember2.setPhoneNumber("0987654321");
    }

    @Test
    public void testListAllMembers() throws Exception {
        // Arrange
        List<Member> members = Arrays.asList(testMember1, testMember2);
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(members);

        // Act & Assert
        mockMvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));

        verify(memberRepository).findAllByOrderByNameAsc();
    }

    @Test
    public void testLookupMemberById() throws Exception {
        // Arrange
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember1));

        // Act & Assert
        mockMvc.perform(get("/api/members/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));

        verify(memberRepository).findById(1L);
    }

    @Test
    public void testLookupMemberById_NotFound() throws Exception {
        // Arrange
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/members/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(memberRepository).findById(99L);
    }

    @Test
    public void testCreateMember() throws Exception {
        // Arrange
        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("new.user@example.com");
        newMember.setPhoneNumber("5551234567");

        doNothing().when(memberRegistrationService).register(any(Member.class));

        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isOk());

        verify(memberRegistrationService).register(any(Member.class));
    }

    @Test
    public void testCreateMember_ValidationError() throws Exception {
        // Arrange
        Member invalidMember = new Member();
        invalidMember.setName(""); // Invalid: name is required
        invalidMember.setEmail("invalid-email"); // Invalid: not a valid email
        invalidMember.setPhoneNumber("123"); // Invalid: too short

        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists());

        verify(memberRegistrationService, never()).register(any(Member.class));
    }

    @Test
    public void testCreateMember_EmailTaken() throws Exception {
        // Arrange
        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("existing@example.com");
        newMember.setPhoneNumber("5551234567");

        doThrow(new ValidationException("Email already exists"))
            .when(memberRegistrationService).register(any(Member.class));

        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(memberRegistrationService).register(any(Member.class));
    }
}
