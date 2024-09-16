package com.example.kitchensink;

import com.example.kitchensink.controller.MemberController;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberRegistrationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRegistrationService memberRegistrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("John Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    void testListAllMembers() throws Exception {
        List<Member> members = Arrays.asList(testMember);
        when(memberRegistrationService.findAllMembers()).thenReturn(members);

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testMember.getId()))
                .andExpect(jsonPath("$[0].name").value(testMember.getName()))
                .andExpect(jsonPath("$[0].email").value(testMember.getEmail()))
                .andExpect(jsonPath("$[0].phoneNumber").value(testMember.getPhoneNumber()));

        verify(memberRegistrationService).findAllMembers();
    }

    @Test
    void testCreateMember() throws Exception {
        when(memberRegistrationService.register(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testMember.getId()))
                .andExpect(jsonPath("$.name").value(testMember.getName()))
                .andExpect(jsonPath("$.email").value(testMember.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(testMember.getPhoneNumber()));

        verify(memberRegistrationService).register(any(Member.class));
    }

    @Test
    void testGetMember() throws Exception {
        when(memberRegistrationService.findMemberById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testMember.getId()))
                .andExpect(jsonPath("$.name").value(testMember.getName()))
                .andExpect(jsonPath("$.email").value(testMember.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(testMember.getPhoneNumber()));

        verify(memberRegistrationService).findMemberById(1L);
    }

    @Test
    void testUpdateMember() throws Exception {
        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setName("Jane Doe");
        updatedMember.setEmail("jane.doe@example.com");
        updatedMember.setPhoneNumber("9876543210");

        when(memberRegistrationService.updateMember(eq(1L), any(Member.class))).thenReturn(updatedMember);

        mockMvc.perform(put("/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedMember.getId()))
                .andExpect(jsonPath("$.name").value(updatedMember.getName()))
                .andExpect(jsonPath("$.email").value(updatedMember.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(updatedMember.getPhoneNumber()));

        verify(memberRegistrationService).updateMember(eq(1L), any(Member.class));
    }

    @Test
    void testDeleteMember() throws Exception {
        doNothing().when(memberRegistrationService).deleteMember(1L);

        mockMvc.perform(delete("/members/1"))
                .andExpect(status().isNoContent());

        verify(memberRegistrationService).deleteMember(1L);
    }

    @Test
    void testGetMemberNotFound() throws Exception {
        when(memberRegistrationService.findMemberById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/members/99"))
                .andExpect(status().isNotFound());

        verify(memberRegistrationService).findMemberById(99L);
    }

    @Test
    void testCreateMemberWithInvalidData() throws Exception {
        Member invalidMember = new Member();
        // Leave all fields empty to trigger validation errors

        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());

        verify(memberRegistrationService, never()).register(any(Member.class));
    }
}