package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;
import javax.persistence.EntityManager;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class MemberRegistrationTest {

    @Mock
    private Logger log;

    @Mock
    private EntityManager em;

    @Mock
    private Event<Member> memberEventSrc;

    @Mock
    private MemberRepository memberRepository;

    private MemberRegistration memberRegistration;

    @BeforeEach
    public void setup() {
        memberRegistration = new MemberRegistration();
        memberRegistration.setLog(log);
        memberRegistration.setEm(em);
        memberRegistration.setMemberEventSrc(memberEventSrc);
        memberRegistration.setMemberRepository(memberRepository);
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        // Arrange
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(null);

        // Act
        memberRegistration.register(member);

        // Assert
        verify(em).persist(member);
        verify(memberEventSrc).fire(member);
        verify(log).info(anyString());
    }

    @Test
    public void testDuplicateEmailRegistration() {
        // Arrange
        Member existingMember = new Member();
        existingMember.setEmail("john@example.com");

        Member newMember = new Member();
        newMember.setName("John Doe");
        newMember.setEmail("john@example.com");
        newMember.setPhoneNumber("1234567890");

        when(memberRepository.findByEmail(newMember.getEmail())).thenReturn(existingMember);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistration.register(newMember);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(em, never()).persist(any(Member.class));
        verify(memberEventSrc, never()).fire(any(Member.class));
    }

    @Test
    public void testInvalidDataRegistration() {
        // Arrange
        Member invalidMember = new Member();
        // Leave all fields empty to simulate invalid data

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistration.register(invalidMember);
        });

        assertTrue(exception.getMessage().contains("Invalid"));
        verify(em, never()).persist(any(Member.class));
        verify(memberEventSrc, never()).fire(any(Member.class));
    }
}