package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link MemberService} class.
 * Tests all public methods using JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private List<Member> memberList;

    @BeforeEach
    void setUp() {
        // Create test member
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("Test User");
        testMember.setEmail("test.user@example.com");
        testMember.setPhoneNumber("1234567890");

        // Create test member list
        Member member1 = new Member();
        member1.setId(1L);
        member1.setName("John Doe");
        member1.setEmail("john.doe@example.com");
        member1.setPhoneNumber("1234567890");

        Member member2 = new Member();
        member2.setId(2L);
        member2.setName("Jane Smith");
        member2.setEmail("jane.smith@example.com");
        member2.setPhoneNumber("9876543210");

        memberList = List.of(member1, member2);
    }

    @Test
    @DisplayName("registerMember should save and return member when validation passes")
    void registerMemberShouldSaveAndReturnMemberWhenValidationPasses() {
        // Given
        Set<ConstraintViolation<Member>> emptyViolations = new HashSet<>();
        when(validator.validate(testMember)).thenReturn(emptyViolations);
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(null);
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // When
        Member savedMember = memberService.registerMember(testMember);

        // Then
        assertNotNull(savedMember);
        assertEquals(testMember.getName(), savedMember.getName());
        assertEquals(testMember.getEmail(), savedMember.getEmail());
        assertEquals(testMember.getPhoneNumber(), savedMember.getPhoneNumber());
        verify(validator, times(1)).validate(testMember);
        verify(memberRepository, times(1)).findByEmail(testMember.getEmail());
        verify(memberRepository, times(1)).save(testMember);
    }

    @Test
    @DisplayName("registerMember should throw ConstraintViolationException when validation fails")
    void registerMemberShouldThrowConstraintViolationExceptionWhenValidationFails() {
        // Given
        Set<ConstraintViolation<Member>> violations = new HashSet<>();
        ConstraintViolation<Member> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        when(validator.validate(testMember)).thenReturn(violations);

        // When & Then
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> memberService.registerMember(testMember)
        );

        assertNotNull(exception);
        verify(validator, times(1)).validate(testMember);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("registerMember should throw ValidationException when email already exists")
    void registerMemberShouldThrowValidationExceptionWhenEmailAlreadyExists() {
        // Given
        Set<ConstraintViolation<Member>> emptyViolations = new HashSet<>();
        when(validator.validate(testMember)).thenReturn(emptyViolations);
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(new Member()); // Email exists

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> memberService.registerMember(testMember)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(validator, times(1)).validate(testMember);
        verify(memberRepository, times(1)).findByEmail(testMember.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("findAllMembers should return list of all members ordered by name")
    void findAllMembersShouldReturnListOfAllMembersOrderedByName() {
        // Given
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(memberList);

        // When
        List<Member> result = memberService.findAllMembers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(memberList, result);
        verify(memberRepository, times(1)).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("findMemberById should return member when found")
    void findMemberByIdShouldReturnMemberWhenFound() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // When
        Member result = memberService.findMemberById(memberId);

        // Then
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getName(), result.getName());
        assertEquals(testMember.getEmail(), result.getEmail());
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("findMemberById should throw EntityNotFoundException when member not found")
    void findMemberByIdShouldThrowEntityNotFoundExceptionWhenMemberNotFound() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> memberService.findMemberById(memberId)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Member not found"));
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("findMemberByEmail should return member when found")
    void findMemberByEmailShouldReturnMemberWhenFound() {
        // Given
        String email = "test.user@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(testMember);

        // When
        Member result = memberService.findMemberByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getName(), result.getName());
        assertEquals(testMember.getEmail(), result.getEmail());
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("findMemberByEmail should return null when member not found")
    void findMemberByEmailShouldReturnNullWhenMemberNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(null);

        // When
        Member result = memberService.findMemberByEmail(email);

        // Then
        assertThat(result).isNull();
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("isEmailAlreadyTaken should return true when email exists")
    void isEmailAlreadyTakenShouldReturnTrueWhenEmailExists() {
        // Given
        String email = "existing@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(new Member());

        // When
        boolean result = memberService.isEmailAlreadyTaken(email);

        // Then
        assertTrue(result);
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("isEmailAlreadyTaken should return false when email does not exist")
    void isEmailAlreadyTakenShouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        String email = "new@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(null);

        // When
        boolean result = memberService.isEmailAlreadyTaken(email);

        // Then
        assertFalse(result);
        verify(memberRepository, times(1)).findByEmail(email);
    }
}
