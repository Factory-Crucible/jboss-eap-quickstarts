package com.example.kitchensink.service;

import com.example.kitchensink.exception.DuplicateEmailException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private MemberService memberService;

    private Member validMember;
    private final Long memberId = 1L;
    private final String memberEmail = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        // Create a valid member for testing
        validMember = Member.builder()
                .id(memberId)
                .name("John Doe")
                .email(memberEmail)
                .phoneNumber("1234567890")
                .build();
    }

    @Test
    void register_ValidMember_ShouldRegisterSuccessfully() {
        // Arrange
        when(validator.validate(any(Member.class))).thenReturn(Collections.emptySet());
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(validMember);

        // Act
        Member registeredMember = memberService.register(validMember);

        // Assert
        assertNotNull(registeredMember);
        assertEquals(memberId, registeredMember.getId());
        assertEquals(memberEmail, registeredMember.getEmail());
        
        verify(validator).validate(validMember);
        verify(memberRepository).findByEmail(memberEmail);
        verify(memberRepository).save(validMember);
    }

    @Test
    void register_ValidationFailure_ShouldThrowConstraintViolationException() {
        // Arrange
        Set<ConstraintViolation<Member>> violations = new HashSet<>();
        @SuppressWarnings("unchecked")
        ConstraintViolation<Member> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        
        when(validator.validate(any(Member.class))).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> memberService.register(validMember));
        
        verify(validator).validate(validMember);
        verify(memberRepository, never()).findByEmail(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void register_DuplicateEmail_ShouldThrowDuplicateEmailException() {
        // Arrange
        when(validator.validate(any(Member.class))).thenReturn(Collections.emptySet());
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(validMember));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> memberService.register(validMember));
        
        verify(validator).validate(validMember);
        verify(memberRepository).findByEmail(memberEmail);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void findAllOrderedByName_ShouldReturnAllMembersOrdered() {
        // Arrange
        List<Member> expectedMembers = Arrays.asList(
                validMember,
                Member.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").phoneNumber("0987654321").build()
        );
        when(memberRepository.findAllOrderedByName()).thenReturn(expectedMembers);

        // Act
        List<Member> actualMembers = memberService.findAllOrderedByName();

        // Assert
        assertEquals(expectedMembers.size(), actualMembers.size());
        assertEquals(expectedMembers, actualMembers);
        
        verify(memberRepository).findAllOrderedByName();
    }

    @Test
    void findById_ExistingId_ShouldReturnMember() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(validMember));

        // Act
        Member foundMember = memberService.findById(memberId);

        // Assert
        assertNotNull(foundMember);
        assertEquals(memberId, foundMember.getId());
        assertEquals(memberEmail, foundMember.getEmail());
        
        verify(memberRepository).findById(memberId);
    }

    @Test
    void findById_NonExistingId_ShouldThrowEntityNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(memberRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> memberService.findById(nonExistingId));
        
        verify(memberRepository).findById(nonExistingId);
    }

    @Test
    void findByEmail_ExistingEmail_ShouldReturnMember() {
        // Arrange
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(validMember));

        // Act
        Optional<Member> foundMember = memberService.findByEmail(memberEmail);

        // Assert
        assertTrue(foundMember.isPresent());
        assertEquals(memberId, foundMember.get().getId());
        assertEquals(memberEmail, foundMember.get().getEmail());
        
        verify(memberRepository).findByEmail(memberEmail);
    }

    @Test
    void findByEmail_NonExistingEmail_ShouldReturnEmptyOptional() {
        // Arrange
        String nonExistingEmail = "nonexisting@example.com";
        when(memberRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        // Act
        Optional<Member> foundMember = memberService.findByEmail(nonExistingEmail);

        // Assert
        assertFalse(foundMember.isPresent());
        
        verify(memberRepository).findByEmail(nonExistingEmail);
    }

    @Test
    void emailAlreadyExists_ExistingEmail_ShouldReturnTrue() {
        // Arrange
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(validMember));

        // Act
        boolean exists = memberService.emailAlreadyExists(memberEmail);

        // Assert
        assertTrue(exists);
        
        verify(memberRepository).findByEmail(memberEmail);
    }

    @Test
    void emailAlreadyExists_NonExistingEmail_ShouldReturnFalse() {
        // Arrange
        String nonExistingEmail = "nonexisting@example.com";
        when(memberRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        // Act
        boolean exists = memberService.emailAlreadyExists(nonExistingEmail);

        // Assert
        assertFalse(exists);
        
        verify(memberRepository).findByEmail(nonExistingEmail);
    }
}
