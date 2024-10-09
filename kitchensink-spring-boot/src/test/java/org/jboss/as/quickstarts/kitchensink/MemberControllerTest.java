package org.jboss.as.quickstarts.kitchensink;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.as.quickstarts.kitchensink.controller.MemberController;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setName("John Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    void testCreateMember() throws Exception {
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(testMember.getName())))
                .andExpect(jsonPath("$.email", is(testMember.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(testMember.getPhoneNumber())));
    }

    @Test
    void testGetAllMembers() throws Exception {
        memberRepository.save(testMember);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is(testMember.getName())))
                .andExpect(jsonPath("$[0].email", is(testMember.getEmail())))
                .andExpect(jsonPath("$[0].phoneNumber", is(testMember.getPhoneNumber())));
    }

    @Test
    void testGetMemberById() throws Exception {
        Member savedMember = memberRepository.save(testMember);

        mockMvc.perform(get("/api/members/{id}", savedMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(testMember.getName())))
                .andExpect(jsonPath("$.email", is(testMember.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(testMember.getPhoneNumber())));
    }

    @Test
    void testUpdateMember() throws Exception {
        Member savedMember = memberRepository.save(testMember);
        savedMember.setName("Jane Doe");

        mockMvc.perform(put("/api/members/{id}", savedMember.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is(testMember.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(testMember.getPhoneNumber())));
    }

    @Test
    void testDeleteMember() throws Exception {
        Member savedMember = memberRepository.save(testMember);

        mockMvc.perform(delete("/api/members/{id}", savedMember.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/members/{id}", savedMember.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateMemberWithExistingEmail() throws Exception {
        memberRepository.save(testMember);

        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Jane Doe");
        duplicateEmailMember.setEmail(testMember.getEmail());
        duplicateEmailMember.setPhoneNumber("9876543210");

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailMember)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCountMembers() throws Exception {
        memberRepository.save(testMember);

        mockMvc.perform(get("/api/members/count"))
                .andExpect(status().isOk())
                .andExpect(content().string(greaterThanOrEqualTo("1")));
    }
}