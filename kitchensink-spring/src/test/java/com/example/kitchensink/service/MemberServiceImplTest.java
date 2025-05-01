package com.example.kitchensink.service;

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MemberServiceImpl}.
 * These tests verify the business logic of the service layer using mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    
    @Mock
    private MemberRepository memberRepository;
    
    @InjectMocks
    private MemberServiceImpl memberService;
    
    private Member member1;
    private Member member2;
    private List<Member> memberList;
    private Page<Member> memberPage;
    private Pageable pageable;
    
    @BeforeEach
    void setUp() {
        // Create test data
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
        
        memberList = Arrays.asList(member1, member2);
        pageable = PageRequest.of(0, 10);
        memberPage = new PageImpl<>(memberList, pageable, memberList.size());
    }
    
    @Test
    void register_ShouldSaveMember_WhenEmailIsUnique() {
        // Arrange
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
        
        when(memberRepository.existsByEmail(newMember.getEmail())).thenReturn(false);
        when(memberRepository.save(newMember)).thenReturn(savedMember);
        
        // Act
        Member result = memberService.register(newMember);
        
        // Assert
        assertNotNull(result);
        assertEquals(savedMember.getId(), result.getId());
        assertEquals(savedMember.getName(), result.getName());
        assertEquals(savedMember.getEmail(), result.getEmail());
        
        verify(memberRepository).existsByEmail(newMember.getEmail());
        verify(memberRepository).save(newMember);
    }
    
    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        Member newMember = Member.builder()
                .name("New User")
                .email("john.doe@example.com") // Email already exists
                .phoneNumber("1122334455")
                .build();
        
        when(memberRepository.existsByEmail(newMember.getEmail())).thenReturn(true);
        
        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            memberService.register(newMember);
        });
        
        assertEquals("Email already exists: " + newMember.getEmail(), exception.getMessage());
        
        verify(memberRepository).existsByEmail(newMember.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }
    
    @Test
    void findAllMembers_ShouldReturnAllMembers() {
        // Arrange
        when(memberRepository.findAll()).thenReturn(memberList);
        
        // Act
        List<Member> result = memberService.findAllMembers();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(memberList, result);
        
        verify(memberRepository).findAll();
    }
    
    @Test
    void findAllMembers_ShouldReturnEmptyList_WhenNoMembersExist() {
        // Arrange
        when(memberRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<Member> result = memberService.findAllMembers();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(memberRepository).findAll();
    }
    
    @Test
    void findAllMembersPageable_ShouldReturnPagedMembers() {
        // Arrange
        when(memberRepository.findAll(pageable)).thenReturn(memberPage);
        
        // Act
        Page<Member> result = memberService.findAllMembers(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(memberPage, result);
        
        verify(memberRepository).findAll(pageable);
    }
    
    @Test
    void findById_ShouldReturnMember_WhenMemberExists() {
        // Arrange
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        
        // Act
        Optional<Member> result = memberService.findById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(member1, result.get());
        
        verify(memberRepository).findById(1L);
    }
    
    @Test
    void findById_ShouldReturnEmptyOptional_WhenMemberDoesNotExist() {
        // Arrange
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Member> result = memberService.findById(999L);
        
        // Assert
        assertFalse(result.isPresent());
        
        verify(memberRepository).findById(999L);
    }
    
    @Test
    void findByEmail_ShouldReturnMember_WhenMemberExists() {
        // Arrange
        when(memberRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(member1));
        
        // Act
        Optional<Member> result = memberService.findByEmail("john.doe@example.com");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(member1, result.get());
        
        verify(memberRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void findByEmail_ShouldReturnEmptyOptional_WhenMemberDoesNotExist() {
        // Arrange
        when(memberRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // Act
        Optional<Member> result = memberService.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(result.isPresent());
        
        verify(memberRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void findByNameContaining_ShouldReturnMembers_WhenNameMatches() {
        // Arrange
        when(memberRepository.findByNameContainingIgnoreCase("Doe")).thenReturn(memberList);
        
        // Act
        List<Member> result = memberService.findByNameContaining("Doe");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(memberList, result);
        
        verify(memberRepository).findByNameContainingIgnoreCase("Doe");
    }
    
    @Test
    void findByNameContaining_ShouldReturnEmptyList_WhenNoNameMatches() {
        // Arrange
        when(memberRepository.findByNameContainingIgnoreCase("Nonexistent")).thenReturn(Collections.emptyList());
        
        // Act
        List<Member> result = memberService.findByNameContaining("Nonexistent");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(memberRepository).findByNameContainingIgnoreCase("Nonexistent");
    }
    
    @Test
    void isEmailUnique_ShouldReturnTrue_WhenEmailDoesNotExist() {
        // Arrange
        when(memberRepository.existsByEmail("new.email@example.com")).thenReturn(false);
        
        // Act
        boolean result = memberService.isEmailUnique("new.email@example.com");
        
        // Assert
        assertTrue(result);
        
        verify(memberRepository).existsByEmail("new.email@example.com");
    }
    
    @Test
    void isEmailUnique_ShouldReturnFalse_WhenEmailExists() {
        // Arrange
        when(memberRepository.existsByEmail("john.doe@example.com")).thenReturn(true);
        
        // Act
        boolean result = memberService.isEmailUnique("john.doe@example.com");
        
        // Assert
        assertFalse(result);
        
        verify(memberRepository).existsByEmail("john.doe@example.com");
    }
    
    @Test
    void update_ShouldUpdateMember_WhenMemberExistsAndEmailIsUnchanged() {
        // Arrange
        Member updatedMember = Member.builder()
                .id(1L)
                .name("John Doe Updated")
                .email("john.doe@example.com") // Same email
                .phoneNumber("1234567890")
                .build();
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.save(updatedMember)).thenReturn(updatedMember);
        
        // Act
        Member result = memberService.update(updatedMember);
        
        // Assert
        assertNotNull(result);
        assertEquals("John Doe Updated", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        
        verify(memberRepository).findById(1L);
        verify(memberRepository).save(updatedMember);
    }
    
    @Test
    void update_ShouldUpdateMember_WhenMemberExistsAndNewEmailIsUnique() {
        // Arrange
        Member updatedMember = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("john.new@example.com") // New email
                .phoneNumber("1234567890")
                .build();
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.existsByEmail("john.new@example.com")).thenReturn(false);
        when(memberRepository.save(updatedMember)).thenReturn(updatedMember);
        
        // Act
        Member result = memberService.update(updatedMember);
        
        // Assert
        assertNotNull(result);
        assertEquals("john.new@example.com", result.getEmail());
        
        verify(memberRepository).findById(1L);
        verify(memberRepository).save(updatedMember);
    }
    
    @Test
    void update_ShouldThrowException_WhenMemberDoesNotExist() {
        // Arrange
        Member nonExistentMember = Member.builder()
                .id(999L)
                .name("Nonexistent")
                .email("nonexistent@example.com")
                .phoneNumber("9999999999")
                .build();
        
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.update(nonExistentMember);
        });
        
        assertEquals("Member not found with id : '999'", exception.getMessage());
        
        verify(memberRepository).findById(999L);
        verify(memberRepository, never()).save(any(Member.class));
    }
    
    @Test
    void update_ShouldThrowException_WhenNewEmailAlreadyExists() {
        // Arrange
        Member updatedMember = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("jane.doe@example.com") // Email belongs to another member
                .phoneNumber("1234567890")
                .build();
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);
        
        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            memberService.update(updatedMember);
        });
        
        assertEquals("Email already exists: jane.doe@example.com", exception.getMessage());
        
        verify(memberRepository).findById(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }
    
    @Test
    void delete_ShouldDeleteMember_WhenMemberExists() {
        // Arrange
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);
        
        // Act
        memberService.delete(1L);
        
        // Assert
        verify(memberRepository).existsById(1L);
        verify(memberRepository).deleteById(1L);
    }
    
    @Test
    void delete_ShouldThrowException_WhenMemberDoesNotExist() {
        // Arrange
        when(memberRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.delete(999L);
        });
        
        assertEquals("Member not found with id : '999'", exception.getMessage());
        
        verify(memberRepository).existsById(999L);
        verify(memberRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void searchMembers_ShouldReturnMatchingMembers() {
        // Arrange
        String searchTerm = "Doe";
        when(memberRepository.searchMembers(searchTerm, pageable)).thenReturn(memberPage);
        
        // Act
        Page<Member> result = memberService.searchMembers(searchTerm, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(memberPage, result);
        
        verify(memberRepository).searchMembers(searchTerm, pageable);
    }
}
