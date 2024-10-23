package org.jboss.as.quickstarts.kitchensink.test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MemberResourceRESTService.class)
public class MemberResourceRESTServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("John Doe");
        testMember.setEmail("john@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    void testListAllMembers() throws Exception {
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findAll()).thenReturn(members);

        mockMvc.perform(get("/rest/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testMember.getId()))
                .andExpect(jsonPath("$[0].name").value(testMember.getName()))
                .andExpect(jsonPath("$[0].email").value(testMember.getEmail()))
                .andExpect(jsonPath("$[0].phoneNumber").value(testMember.getPhoneNumber()));
    }

    @Test
    void testLookupMemberById() throws Exception {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/rest/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testMember.getId()))
                .andExpect(jsonPath("$.name").value(testMember.getName()))
                .andExpect(jsonPath("$.email").value(testMember.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(testMember.getPhoneNumber()));
    }

    @Test
    void testLookupMemberByIdNotFound() throws Exception {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rest/members/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateMember() throws Exception {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(post("/rest/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/rest/members/1"));
    }

    @Test
    void testCreateMemberInvalidData() throws Exception {
        Member invalidMember = new Member();
        invalidMember.setName(""); // Invalid: empty name

        mockMvc.perform(post("/rest/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMember() throws Exception {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        testMember.setName("Jane Doe");

        mockMvc.perform(put("/rest/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void testUpdateMemberNotFound() throws Exception {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/rest/members/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteMember() throws Exception {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        doNothing().when(memberRepository).delete(testMember);

        mockMvc.perform(delete("/rest/members/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMemberNotFound() throws Exception {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/rest/members/99"))
                .andExpect(status().isNotFound());
    }
}