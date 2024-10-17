package com.example.kitchensink;

import com.example.kitchensink.controller.MemberController;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member(1L, "John Doe", "john@example.com", "1234567890");
    }

    @Test
    void getAllMembers_ShouldReturnListOfMembers() throws Exception {
        when(memberService.getAllMembers()).thenReturn(Arrays.asList(testMember));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(memberService).getAllMembers();
    }

    @Test
    void getMemberById_WithValidId_ShouldReturnMember() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService).getMemberById(1L);
    }

    @Test
    void getMemberById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(memberService.getMemberById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/99"))
                .andExpect(status().isNotFound());

        verify(memberService).getMemberById(99L);
    }

    @Test
    void createMember_WithValidData_ShouldReturnCreatedMember() throws Exception {
        when(memberService.register(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService).register(any(Member.class));
    }

    @Test
    void createMember_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Member invalidMember = new Member(null, "", "invalid-email", "123");

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).register(any(Member.class));
    }

    @Test
    void updateMember_WithValidData_ShouldReturnUpdatedMember() throws Exception {
        when(memberService.updateMember(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(put("/api/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService).updateMember(any(Member.class));
    }

    @Test
    void updateMember_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(memberService.updateMember(any(Member.class))).thenThrow(new Exception("Member not found"));

        mockMvc.perform(put("/api/members/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isNotFound());

        verify(memberService).updateMember(any(Member.class));
    }

    @Test
    void deleteMember_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(memberService).deleteMember(1L);

        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isOk());

        verify(memberService).deleteMember(1L);
    }

    @Test
    void deleteMember_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        doThrow(new Exception("Member not found")).when(memberService).deleteMember(99L);

        mockMvc.perform(delete("/api/members/99"))
                .andExpect(status().isNotFound());

        verify(memberService).deleteMember(99L);
    }

    @Test
    void getMemberByEmail_WithValidEmail_ShouldReturnMember() throws Exception {
        when(memberService.getMemberByEmail("john@example.com")).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/api/members/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(memberService).getMemberByEmail("john@example.com");
    }

    @Test
    void getMemberByEmail_WithNonExistentEmail_ShouldReturnNotFound() throws Exception {
        when(memberService.getMemberByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());

        verify(memberService).getMemberByEmail("nonexistent@example.com");
    }
}