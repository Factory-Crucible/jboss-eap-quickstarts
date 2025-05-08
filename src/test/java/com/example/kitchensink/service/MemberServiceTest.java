package com.example.kitchensink.service;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MemberService} implementation.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // Create a test member
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("Test User");
        testMember.setEmail("test@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    void findAll_ShouldReturnAllMembers() {
        // Arrange
        Member member1 = new Member();
        member1.setId(1L);
        member1.setName("User 1");

        Member member2 = new Member();
        member2.setId(2L);
        member2.setName("User 2");

        List<Member> expectedMembers = Arrays.asList(member1, member2);
        when(memberRepository.findAll()).thenReturn(expectedMembers);

        // Act
        List<Member> actualMembers = memberService.findAll();

        // Assert
        assertEquals(expectedMembers.size(), actualMembers.size());
        assertEquals(expectedMembers, actualMembers);
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void findById_WithExistingId_ShouldReturnMember() {
        // Arrange
        Long id = 1L;
        when(memberRepository.findById(id)).thenReturn(Optional.of(testMember));

        // Act
        Optional<Member> result = memberService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testMember, result.get());
        verify(memberRepository, times(1)).findById(id);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // Arrange
        Long id = 999L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Member> result = memberService.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(memberRepository, times(1)).findById(id);
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnMember() {
        // Arrange
        String email = "test@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(testMember));

        // Act
        Optional<Member> result = memberService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testMember, result.get());
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmptyOptional() {
        // Arrange
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<Member> result = memberService.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    void register_WithValidMember_ShouldSaveMemberAndPublishEvent() {
        // Arrange
        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("new@example.com");
        newMember.setPhoneNumber("9876543210");

        Member savedMember = new Member();
        savedMember.setId(2L);
        savedMember.setName(newMember.getName());
        savedMember.setEmail(newMember.getEmail());
        savedMember.setPhoneNumber(newMember.getPhoneNumber());

        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // Act
        Member result = memberService.register(newMember);

        // Assert
        assertNotNull(result);
        assertEquals(savedMember.getId(), result.getId());
        assertEquals(savedMember.getName(), result.getName());
        assertEquals(savedMember.getEmail(), result.getEmail());

        verify(memberRepository, times(1)).existsByEmail(newMember.getEmail());
        verify(memberRepository, times(1)).save(newMember);

        ArgumentCaptor<MemberRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(MemberRegisteredEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        assertEquals(savedMember, eventCaptor.getValue().getMember());
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowDuplicateResourceException() {
        // Arrange
        Member newMember = new Member();
        newMember.setName("New User");
        newMember.setEmail("existing@example.com");
        newMember.setPhoneNumber("9876543210");

        when(memberRepository.existsByEmail(newMember.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> memberService.register(newMember));
        verify(memberRepository, times(1)).existsByEmail(newMember.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
        verify(eventPublisher, never()).publishEvent(any(MemberRegisteredEvent.class));
    }

    @Test
    void update_WithExistingIdAndValidData_ShouldUpdateMember() {
        // Arrange
        Long id = 1L;
        Member updatedDetails = new Member();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setPhoneNumber("5555555555");

        Member existingMember = new Member();
        existingMember.setId(id);
        existingMember.setName("Original Name");
        existingMember.setEmail("original@example.com");
        existingMember.setPhoneNumber("1234567890");

        Member updatedMember = new Member();
        updatedMember.setId(id);
        updatedMember.setName(updatedDetails.getName());
        updatedMember.setEmail(updatedDetails.getEmail());
        updatedMember.setPhoneNumber(updatedDetails.getPhoneNumber());

        when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
        when(memberRepository.existsByEmail(updatedDetails.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        // Act
        Member result = memberService.update(id, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(updatedDetails.getName(), result.getName());
        assertEquals(updatedDetails.getEmail(), result.getEmail());
        assertEquals(updatedDetails.getPhoneNumber(), result.getPhoneNumber());

        verify(memberRepository, times(1)).findById(id);
        verify(memberRepository, times(1)).save(existingMember);
    }

    @Test
    void update_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        Long id = 999L;
        Member updatedDetails = new Member();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setPhoneNumber("5555555555");

        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> memberService.update(id, updatedDetails));
        verify(memberRepository, times(1)).findById(id);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void update_WithDuplicateEmail_ShouldThrowDuplicateResourceException() {
        // Arrange
        Long id = 1L;
        Member updatedDetails = new Member();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("duplicate@example.com");
        updatedDetails.setPhoneNumber("5555555555");

        Member existingMember = new Member();
        existingMember.setId(id);
        existingMember.setName("Original Name");
        existingMember.setEmail("original@example.com");
        existingMember.setPhoneNumber("1234567890");

        when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
        when(memberRepository.existsByEmail(updatedDetails.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> memberService.update(id, updatedDetails));
        verify(memberRepository, times(1)).findById(id);
        verify(memberRepository, times(1)).existsByEmail(updatedDetails.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void update_WithSameEmail_ShouldNotCheckDuplicate() {
        // Arrange
        Long id = 1L;
        String email = "same@example.com";
        
        Member updatedDetails = new Member();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail(email);
        updatedDetails.setPhoneNumber("5555555555");

        Member existingMember = new Member();
        existingMember.setId(id);
        existingMember.setName("Original Name");
        existingMember.setEmail(email); // Same email
        existingMember.setPhoneNumber("1234567890");

        Member updatedMember = new Member();
        updatedMember.setId(id);
        updatedMember.setName(updatedDetails.getName());
        updatedMember.setEmail(email);
        updatedMember.setPhoneNumber(updatedDetails.getPhoneNumber());

        when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        // Act
        Member result = memberService.update(id, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals(updatedDetails.getName(), result.getName());
        verify(memberRepository, never()).existsByEmail(anyString()); // Should not check for duplicate email
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void delete_WithExistingId_ShouldDeleteAndReturnTrue() {
        // Arrange
        Long id = 1L;
        when(memberRepository.existsById(id)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(id);

        // Act
        boolean result = memberService.delete(id);

        // Assert
        assertTrue(result);
        verify(memberRepository, times(1)).existsById(id);
        verify(memberRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_WithNonExistingId_ShouldReturnFalse() {
        // Arrange
        Long id = 999L;
        when(memberRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = memberService.delete(id);

        // Assert
        assertFalse(result);
        verify(memberRepository, times(1)).existsById(id);
        verify(memberRepository, never()).deleteById(anyLong());
    }
}
