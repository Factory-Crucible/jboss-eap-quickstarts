package com.migration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migration.model.Member;
import com.migration.repository.MemberRepository;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private List<Member> testMembers;

    @BeforeEach
    void setUp() {
        testMember = new Member(1L, "John Doe", "john@example.com", "1234567890");
        testMembers = Arrays.asList(
            testMember,
            new Member(2L, "Jane Doe", "jane@example.com", "0987654321")
        );
    }

    @Test
    void getAllMembers_ShouldReturnListOfMembers() throws Exception {
        when(memberRepository.findAll()).thenReturn(testMembers);

        mockMvc.perform(get("/api/members"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].name", is("John Doe")))
               .andExpect(jsonPath("$[1].name", is("Jane Doe")));

        verify(memberRepository).findAll();
    }

    @Test
    void getMemberById_WithValidId_ShouldReturnMember() throws Exception {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/api/members/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("John Doe")))
               .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(memberRepository).findById(1L);
    }

    @Test
    void getMemberById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/99"))
               .andExpect(status().isNotFound());

        verify(memberRepository).findById(99L);
    }

    @Test
    void createMember_WithValidData_ShouldReturnCreatedMember() throws Exception {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(post("/api/members")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testMember)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.name", is("John Doe")))
               .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void updateMember_WithValidData_ShouldReturnUpdatedMember() throws Exception {
        Member updatedMember = new Member(1L, "John Updated", "john.updated@example.com", "1234567890");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        mockMvc.perform(put("/api/members/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(updatedMember)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("John Updated")))
               .andExpect(jsonPath("$.email", is("john.updated@example.com")));

        verify(memberRepository).findById(1L);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void deleteMember_WithValidId_ShouldReturnNoContent() throws Exception {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(delete("/api/members/1"))
               .andExpect(status().isOk());

        verify(memberRepository).findById(1L);
        verify(memberRepository).delete(testMember);
    }

    @Test
    void getMemberByEmail_WithValidEmail_ShouldReturnMember() throws Exception {
        when(memberRepository.findByEmail("john@example.com")).thenReturn(testMember);

        mockMvc.perform(get("/api/members/email/john@example.com"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("John Doe")))
               .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(memberRepository).findByEmail("john@example.com");
    }

    @Test
    void searchMembersByName_WithValidName_ShouldReturnMatchingMembers() throws Exception {
        when(memberRepository.findByNameContainingIgnoreCase("Doe")).thenReturn(testMembers);

        mockMvc.perform(get("/api/members/search").param("name", "Doe"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].name", is("John Doe")))
               .andExpect(jsonPath("$[1].name", is("Jane Doe")));

        verify(memberRepository).findByNameContainingIgnoreCase("Doe");
    }
}