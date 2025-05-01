package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberDTO;
import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link MemberController}.
 * These tests verify the REST API endpoints using MockMvc to simulate HTTP requests
 * and mock the service layer to isolate the controller layer.
 */
@WebMvcTest(MemberController.class)
class MemberControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MemberService memberService;
    
    private Member member1;
    private Member member2;
    private MemberDTO memberDTO1;
    private MemberDTO memberDTO2;
    private List<Member> memberList;
    private Page<Member> memberPage;
    
    @BeforeEach
    void setUp() {
        // Create test data - entities
        member1 = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();
        
        member2 = Member.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phoneNumber("0987654321")
                .build();
        
        // Create test data - DTOs
        memberDTO1 = MemberDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();
        
        memberDTO2 = MemberDTO.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phoneNumber("0987654321")
                .build();
        
        memberList = Arrays.asList(member1, member2);
        Pageable pageable = PageRequest.of(0, 10);
        memberPage = new PageImpl<>(memberList, pageable, memberList.size());
    }
    
    @Test
    void getAllMembers_ShouldReturnAllMembers() throws Exception {
        // Arrange
        when(memberService.findAllMembers()).thenReturn(memberList);
        
        // Act & Assert
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")))
                .andExpect(jsonPath("$[1].email", is("jane.doe@example.com")));
        
        verify(memberService).findAllMembers();
    }
    
    @Test
    void getAllMembers_ShouldReturnEmptyList_WhenNoMembersExist() throws Exception {
        // Arrange
        when(memberService.findAllMembers()).thenReturn(Collections.emptyList());
        
        // Act & Assert
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(memberService).findAllMembers();
    }
    
    @Test
    void getPagedMembers_ShouldReturnPagedMembers() throws Exception {
        // Arrange
        when(memberService.findAllMembers(any(Pageable.class))).thenReturn(memberPage);
        
        // Act & Assert
        mockMvc.perform(get("/api/members/paged")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)));
        
        verify(memberService).findAllMembers(any(Pageable.class));
    }
    
    @Test
    void getMemberById_ShouldReturnMember_WhenMemberExists() throws Exception {
        // Arrange
        when(memberService.findById(1L)).thenReturn(Optional.of(member1));
        
        // Act & Assert
        mockMvc.perform(get("/api/members/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
        
        verify(memberService).findById(1L);
    }
    
    @Test
    void getMemberById_ShouldReturnNotFound_WhenMemberDoesNotExist() throws Exception {
        // Arrange
        when(memberService.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/members/{id}", 999L))
                .andExpect(status().isNotFound());
        
        verify(memberService).findById(999L);
    }
    
    @Test
    void createMember_ShouldReturnCreatedMember() throws Exception {
        // Arrange
        MemberDTO newMemberDTO = MemberDTO.builder()
                .name("New User")
                .email("new.user@example.com")
                .phoneNumber("1122334455")
                .build();
        
        Member newMember = Member.builder()
                .name("New User")
                .email("new.user@example.com")
                .phoneNumber("1122334455")
                .build();
        
        Member savedMember = Member.builder()
                .id(3L)
                .name("New User")
                .email("new.user@example.com")
                .phoneNumber("1122334455")
                .build();
        
        when(memberService.register(any(Member.class))).thenReturn(savedMember);
        
        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMemberDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("new.user@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("1122334455")));
        
        verify(memberService).register(any(Member.class));
    }
    
    @Test
    void createMember_ShouldReturnBadRequest_WhenIdIsProvided() throws Exception {
        // Arrange
        MemberDTO invalidMemberDTO = MemberDTO.builder()
                .id(3L) // ID should not be provided for creation
                .name("New User")
                .email("new.user@example.com")
                .phoneNumber("1122334455")
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMemberDTO)))
                .andExpect(status().isBadRequest());
        
        verify(memberService, never()).register(any(Member.class));
    }
    
    @Test
    void createMember_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Arrange
        MemberDTO invalidMemberDTO = MemberDTO.builder()
                .name("") // Empty name - validation should fail
                .email("invalid-email") // Invalid email - validation should fail
                .phoneNumber("123") // Too short - validation should fail
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMemberDTO)))
                .andExpect(status().isBadRequest());
        
        verify(memberService, never()).register(any(Member.class));
    }
    
    @Test
    void createMember_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        // Arrange
        MemberDTO newMemberDTO = MemberDTO.builder()
                .name("New User")
                .email("john.doe@example.com") // Email already exists
                .phoneNumber("1122334455")
                .build();
        
        when(memberService.register(any(Member.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists: john.doe@example.com"));
        
        // Act & Assert
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMemberDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
        
        verify(memberService).register(any(Member.class));
    }
    
    @Test
    void updateMember_ShouldReturnUpdatedMember_WhenMemberExists() throws Exception {
        // Arrange
        MemberDTO updatedMemberDTO = MemberDTO.builder()
                .id(1L)
                .name("John Doe Updated")
                .email("john.updated@example.com")
                .phoneNumber("9876543210")
                .build();
        
        Member updatedMember = Member.builder()
                .id(1L)
                .name("John Doe Updated")
                .email("john.updated@example.com")
                .phoneNumber("9876543210")
                .build();
        
        when(memberService.findById(1L)).thenReturn(Optional.of(member1));
        when(memberService.update(any(Member.class))).thenReturn(updatedMember);
        
        // Act & Assert
        mockMvc.perform(put("/api/members/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.phoneNumber", is("9876543210")));
        
        verify(memberService).findById(1L);
        verify(memberService).update(any(Member.class));
    }
    
    @Test
    void updateMember_ShouldReturnNotFound_WhenMemberDoesNotExist() throws Exception {
        // Arrange
        MemberDTO updatedMemberDTO = MemberDTO.builder()
                .id(999L)
                .name("Nonexistent User")
                .email("nonexistent@example.com")
                .phoneNumber("9999999999")
                .build();
        
        when(memberService.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(put("/api/members/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMemberDTO)))
                .andExpect(status().isNotFound());
        
        verify(memberService).findById(999L);
        verify(memberService, never()).update(any(Member.class));
    }
    
    @Test
    void updateMember_ShouldReturnBadRequest_WhenIdsMismatch() throws Exception {
        // Arrange
        MemberDTO mismatchedIdDTO = MemberDTO.builder()
                .id(2L) // ID in body doesn't match path variable
                .name("John Doe Updated")
                .email("john.updated@example.com")
                .phoneNumber("9876543210")
                .build();
        
        // Act & Assert
        mockMvc.perform(put("/api/members/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mismatchedIdDTO)))
                .andExpect(status().isBadRequest());
        
        verify(memberService, never()).findById(anyLong());
        verify(memberService, never()).update(any(Member.class));
    }
    
    @Test
    void updateMember_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        // Arrange
        MemberDTO updatedMemberDTO = MemberDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("jane.doe@example.com") // Email belongs to another member
                .phoneNumber("1234567890")
                .build();
        
        when(memberService.findById(1L)).thenReturn(Optional.of(member1));
        when(memberService.update(any(Member.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists: jane.doe@example.com"));
        
        // Act & Assert
        mockMvc.perform(put("/api/members/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMemberDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
        
        verify(memberService).findById(1L);
        verify(memberService).update(any(Member.class));
    }
    
    @Test
    void deleteMember_ShouldReturnNoContent_WhenMemberExists() throws Exception {
        // Arrange
        when(memberService.findById(1L)).thenReturn(Optional.of(member1));
        doNothing().when(memberService).delete(1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/members/{id}", 1L))
                .andExpect(status().isNoContent());
        
        verify(memberService).findById(1L);
        verify(memberService).delete(1L);
    }
    
    @Test
    void deleteMember_ShouldReturnNotFound_WhenMemberDoesNotExist() throws Exception {
        // Arrange
        when(memberService.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(delete("/api/members/{id}", 999L))
                .andExpect(status().isNotFound());
        
        verify(memberService).findById(999L);
        verify(memberService, never()).delete(anyLong());
    }
    
    @Test
    void getMemberByEmail_ShouldReturnMember_WhenMemberExists() throws Exception {
        // Arrange
        when(memberService.findByEmail("john.doe@example.com")).thenReturn(Optional.of(member1));
        
        // Act & Assert
        mockMvc.perform(get("/api/members/by-email/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
        
        verify(memberService).findByEmail("john.doe@example.com");
    }
    
    @Test
    void getMemberByEmail_ShouldReturnNotFound_WhenMemberDoesNotExist() throws Exception {
        // Arrange
        when(memberService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/members/by-email/{email}", "nonexistent@example.com"))
                .andExpect(status().isNotFound());
        
        verify(memberService).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void searchMembersByName_ShouldReturnMatchingMembers() throws Exception {
        // Arrange
        when(memberService.findByNameContaining("Doe")).thenReturn(memberList);
        
        // Act & Assert
        mockMvc.perform(get("/api/members/search")
                .param("name", "Doe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));
        
        verify(memberService).findByNameContaining("Doe");
    }
    
    @Test
    void searchMembersByName_ShouldReturnEmptyList_WhenNoMatchesFound() throws Exception {
        // Arrange
        when(memberService.findByNameContaining("Nonexistent")).thenReturn(Collections.emptyList());
        
        // Act & Assert
        mockMvc.perform(get("/api/members/search")
                .param("name", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(memberService).findByNameContaining("Nonexistent");
    }
    
    @Test
    void advancedSearch_ShouldReturnMatchingMembers() throws Exception {
        // Arrange
        when(memberService.searchMembers(eq("Doe"), any(Pageable.class))).thenReturn(memberPage);
        
        // Act & Assert
        mockMvc.perform(get("/api/members/search/advanced")
                .param("searchTerm", "Doe")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.content[1].name", is("Jane Doe")));
        
        verify(memberService).searchMembers(eq("Doe"), any(Pageable.class));
    }
    
    @Test
    void isEmailAvailable_ShouldReturnTrue_WhenEmailIsUnique() throws Exception {
        // Arrange
        when(memberService.isEmailUnique("new.email@example.com")).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(get("/api/members/check-email")
                .param("email", "new.email@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(memberService).isEmailUnique("new.email@example.com");
    }
    
    @Test
    void isEmailAvailable_ShouldReturnFalse_WhenEmailExists() throws Exception {
        // Arrange
        when(memberService.isEmailUnique("john.doe@example.com")).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(get("/api/members/check-email")
                .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
        
        verify(memberService).isEmailUnique("john.doe@example.com");
    }
}
