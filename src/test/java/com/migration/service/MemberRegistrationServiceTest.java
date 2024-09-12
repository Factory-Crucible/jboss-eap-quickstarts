package com.migration.service;

import com.migration.model.Member;
import com.migration.repository.MemberRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberRegistrationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private MemberRegistrationService memberRegistrationService;

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
    void testRegisterSuccessful() throws Exception {
        when(validator.validate(any(Member.class))).thenReturn(new HashSet<>());
        when(memberRepository.findByEmail(anyString())).thenReturn(null);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        Member registeredMember = memberRegistrationService.register(testMember);

        assertNotNull(registeredMember);
        assertEquals(testMember.getId(), registeredMember.getId());
        verify(memberRepository, times(1)).save(testMember);
    }

    @Test
    void testRegisterExistingEmail() {
        when(memberRepository.findByEmail(anyString())).thenReturn(testMember);

        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistrationService.register(testMember);
        });

        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void testRegisterInvalidMember() {
        Set<ConstraintViolation<Member>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        when(validator.validate(any(Member.class))).thenReturn(violations);

        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistrationService.register(testMember);
        });

        assertTrue(exception.getMessage().contains("Invalid member data"));
    }

    @Test
    void testFindById() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));

        Member foundMember = memberRegistrationService.findById(1L);

        assertNotNull(foundMember);
        assertEquals(testMember.getId(), foundMember.getId());
    }

    @Test
    void testFindByEmail() {
        when(memberRepository.findByEmail(anyString())).thenReturn(testMember);

        Member foundMember = memberRegistrationService.findByEmail("john@example.com");

        assertNotNull(foundMember);
        assertEquals(testMember.getEmail(), foundMember.getEmail());
    }

    @Test
    void testUpdateMember() throws Exception {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        assertDoesNotThrow(() -> memberRegistrationService.update(testMember));
        verify(memberRepository, times(1)).save(testMember);
    }

    @Test
    void testUpdateMemberWithoutId() {
        testMember.setId(null);

        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistrationService.update(testMember);
        });

        assertTrue(exception.getMessage().contains("Member ID is required for update"));
    }

    @Test
    void testDeleteMember() throws Exception {
        doNothing().when(memberRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> memberRegistrationService.delete(1L));
        verify(memberRepository, times(1)).deleteById(1L);
    }
}