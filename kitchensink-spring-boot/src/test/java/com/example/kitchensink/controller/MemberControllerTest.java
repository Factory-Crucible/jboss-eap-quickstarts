package com.example.kitchensink.controller;

import com.example.kitchensink.exception.DuplicateEmailException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private Member testMember;
    private List<Member> testMembers;

    @BeforeEach
    void setUp() {
        // Set up test data
        testMember = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phoneNumber("0987654321")
                .build();

        testMembers = Arrays.asList(testMember, member2);
    }

    @Test
    void listAllMembers_ShouldReturnAllMembers() throws Exception {
        // Arrange
        when(memberService.findAllOrderedByName()).thenReturn(testMembers);

        // Act & Assert
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is("1234567890")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));

        verify(memberService).findAllOrderedByName();
    }

    @Test
    void listAllMembers_NoMembers_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(memberService.findAllOrderedByName()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(memberService).findAllOrderedByName();
    }

    @Test
    void getMemberById_ExistingId_ShouldReturnMember() throws Exception {
        // Arrange
        when(memberService.findById(1L)).thenReturn(testMember);

        // Act & Assert
        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));

        verify(memberService).findById(1L);
    }

    @Test
    void getMemberById_NonExistingId_ShouldReturn404() throws Exception {
        // Arrange
        when(memberService.findById(999L)).thenThrow(new EntityNotFoundException("Member not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound());

        verify(memberService).findById(999L);
    }

    @Test
    void createMember_ValidInput_ShouldCreateAndReturnMember() throws Exception {
        // Arrange
        Member inputMember = Member.builder()
                .name("New User")
                .email("new.user@example.com")
                .phoneNumber("5551234567")
                .build();

        Member savedMember = Member.builder()
                .id(3L)
                .name("New User")
                .email("new.user@example.com")
                .phoneNumber("5551234567")
                .build();

        when(memberService.register(any(Member.class))).thenReturn(savedMember);

        // Act & Assert
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputMember)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/members/3")))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("new.user@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("5551234567")));

        verify(memberService).register(any(Member.class));
    }

    @Test
    void createMember_InvalidInput_ShouldReturn400() throws Exception {
        // Arrange
        Member invalidMember = Member.builder()
                .name("")  // Invalid: empty name
                .email("invalid-email")  // Invalid: not an email
                .phoneNumber("123")  // Invalid: too short
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).register(any(Member.class));
    }

    @Test
    void createMember_DuplicateEmail_ShouldReturn409() throws Exception {
        // Arrange
        Member inputMember = Member.builder()
                .name("Duplicate User")
                .email("john.doe@example.com")  // Already exists
                .phoneNumber("9876543210")
                .build();

        when(memberService.register(any(Member.class)))
                .thenThrow(new DuplicateEmailException("Email already in use: john.doe@example.com"));

        // Act & Assert
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputMember)))
                .andExpect(status().isConflict());

        verify(memberService).register(any(Member.class));
    }
}
