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
    void testRegisterMember() throws Exception {
        when(memberService.register(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));

        verify(memberService, times(1)).register(any(Member.class));
    }

    @Test
    void testRegisterMemberInvalidData() throws Exception {
        Member invalidMember = new Member(null, "", "invalid-email", "123");

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).register(any(Member.class));
    }

    @Test
    void testGetMember() throws Exception {
        when(memberService.findById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));

        verify(memberService, times(1)).findById(1L);
    }

    @Test
    void testGetMemberNotFound() throws Exception {
        when(memberService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/99"))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).findById(99L);
    }

    @Test
    void testGetAllMembers() throws Exception {
        Member member2 = new Member(2L, "Jane Doe", "jane@example.com", "0987654321");
        when(memberService.findAllOrderedByName()).thenReturn(Arrays.asList(testMember, member2));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(memberService, times(1)).findAllOrderedByName();
    }

    @Test
    void testUpdateMember() throws Exception {
        Member updatedMember = new Member(1L, "John Updated", "john.updated@example.com", "1234567891");
        when(memberService.update(eq(1L), any(Member.class))).thenReturn(Optional.of(updatedMember));

        mockMvc.perform(put("/api/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567891"));

        verify(memberService, times(1)).update(eq(1L), any(Member.class));
    }

    @Test
    void testUpdateMemberNotFound() throws Exception {
        when(memberService.update(eq(99L), any(Member.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/members/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).update(eq(99L), any(Member.class));
    }

    @Test
    void testDeleteMember() throws Exception {
        when(memberService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).delete(1L);
    }

    @Test
    void testDeleteMemberNotFound() throws Exception {
        when(memberService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/members/99"))
                .andExpect(status().isNotFound());

        verify(memberService, times(1)).delete(99L);
    }
}