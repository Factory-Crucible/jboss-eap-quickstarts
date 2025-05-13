package com.factory.kitchensink.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.dto.MemberDTO;
import com.factory.kitchensink.exception.EmailAlreadyExistsException;
import com.factory.kitchensink.exception.MemberNotFoundException;
import com.factory.kitchensink.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MemberDTO testMemberDTO1;
    private MemberDTO testMemberDTO2;
    private CreateMemberDTO createMemberDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        testMemberDTO1 = new MemberDTO(1L, "John Doe", "john.doe@example.com", "1234567890");
        testMemberDTO2 = new MemberDTO(2L, "Jane Doe", "jane.doe@example.com", "0987654321");
        createMemberDTO = new CreateMemberDTO("John Doe", "john.doe@example.com", "1234567890");
    }

    @Test
    void getAllMembers_ShouldReturnAllMembers() throws Exception {
        // Arrange
        List<MemberDTO> members = Arrays.asList(testMemberDTO1, testMemberDTO2);
        when(memberService.findAllMembers()).thenReturn(members);

        // Act & Assert
        mockMvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is("1234567890")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")))
                .andExpect(jsonPath("$[1].email", is("jane.doe@example.com")))
                .andExpect(jsonPath("$[1].phoneNumber", is("0987654321")));
    }

    @Test
    void getMemberById_ShouldReturnMember_WhenMemberExists() throws Exception {
        // Arrange
        Long id = 1L;
        when(memberService.findById(id)).thenReturn(testMemberDTO1);

        // Act & Assert
        mockMvc.perform(get("/api/members/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    @Test
    void getMemberById_ShouldReturnNotFound_WhenMemberDoesNotExist() throws Exception {
        // Arrange
        Long id = 999L;
        when(memberService.findById(id)).thenThrow(new MemberNotFoundException("Member not found with id: " + id));

        // Act & Assert
        mockMvc.perform(get("/api/members/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Member not found with id: 999")));
    }

    @Test
    void getMemberByEmail_ShouldReturnMember_WhenMemberExists() throws Exception {
        // Arrange
        String email = "john.doe@example.com";
        when(memberService.findByEmail(email)).thenReturn(testMemberDTO1);

        // Act & Assert
        mockMvc.perform(get("/api/members/email/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    @Test
    void getMemberByEmail_ShouldReturnNotFound_WhenMemberDoesNotExist() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";
        when(memberService.findByEmail(email)).thenThrow(new MemberNotFoundException("Member not found with email: " + email));

        // Act & Assert
        mockMvc.perform(get("/api/members/email/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Member not found with email: nonexistent@example.com")));
    }

    @Test
    void createMember_ShouldCreateMember_WhenDataIsValid() throws Exception {
        // Arrange
        when(memberService.register(any(CreateMemberDTO.class))).thenReturn(testMemberDTO1);

        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/members/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    @Test
    void createMember_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        // Arrange
        when(memberService.register(any(CreateMemberDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists: " + createMemberDTO.getEmail()));

        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("CONFLICT")))
                .andExpect(jsonPath("$.message", is("Email already exists: john.doe@example.com")));
    }

    @Test
    void createMember_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        // Arrange
        CreateMemberDTO invalidMemberDTO = new CreateMemberDTO("", "", "123");

        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMemberDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.phoneNumber").exists());
    }
}
