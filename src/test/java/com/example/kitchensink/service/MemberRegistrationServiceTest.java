package com.example.kitchensink.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;

/**
 * Unit tests for the MemberRegistrationService.
 * These tests verify the business logic of the service using mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
public class MemberRegistrationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MemberRegistrationService memberRegistrationService;

    @BeforeEach
    public void setup() {
        memberRegistrationService = new MemberRegistrationService(memberRepository, eventPublisher);
    }

    @Test
    public void testRegisterMemberSuccess() throws Exception {
        // Arrange
        Member member = createTestMember();
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(null);

        // Act
        memberRegistrationService.register(member);

        // Assert
        verify(memberRepository).save(member);
        
        // Verify event was published
        ArgumentCaptor<MemberRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(MemberRegisteredEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(member, eventCaptor.getValue().getMember());
    }

    @Test
    public void testRegisterMemberWithExistingEmail() {
        // Arrange
        Member existingMember = createTestMember();
        Member newMember = createTestMember();
        
        when(memberRepository.findByEmail(newMember.getEmail())).thenReturn(existingMember);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistrationService.register(newMember);
        });
        
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(memberRepository, never()).save(any(Member.class));
        verify(eventPublisher, never()).publishEvent(any(MemberRegisteredEvent.class));
    }

    @Test
    public void testRegisterMemberRepositoryException() {
        // Arrange
        Member member = createTestMember();
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(null);
        when(memberRepository.save(any(Member.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistrationService.register(member);
        });
        
        assertEquals("Database error", exception.getMessage());
        verify(eventPublisher, never()).publishEvent(any(MemberRegisteredEvent.class));
    }

    /**
     * Helper method to create a test member
     */
    private Member createTestMember() {
        Member member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");
        return member;
    }
}
