package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private Logger log;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Event<Member> memberEventSrc;

    @InjectMocks
    private MemberRegistration memberRegistration;

    private Member member;

    @BeforeEach
    public void setUp() {
        member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");
    }

    @Test
    public void testRegister() throws Exception {
        // Arrange
        doNothing().when(memberRepository).register(any(Member.class));

        // Act
        memberRegistration.register(member);

        // Assert
        verify(memberRepository).register(member);
        verify(memberEventSrc).fire(member);
        verify(log).info(anyString());
    }

    @Test
    public void testRegisterWithNullMember() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> memberRegistration.register(null));
    }

    @Test
    public void testRegisterWithInvalidEmail() {
        // Arrange
        member.setEmail("invalid-email");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> memberRegistration.register(member));
        assertTrue(exception.getMessage().contains("Invalid email"));
    }

    @Test
    public void testRegisterWithDuplicateEmail() throws Exception {
        // Arrange
        doThrow(new Exception("Email already exists")).when(memberRepository).register(any(Member.class));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> memberRegistration.register(member));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    public void testRegisterWithEmptyName() {
        // Arrange
        member.setName("");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> memberRegistration.register(member));
        assertTrue(exception.getMessage().contains("Name is required"));
    }

    @Test
    public void testRegisterWithInvalidPhoneNumber() {
        // Arrange
        member.setPhoneNumber("123");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> memberRegistration.register(member));
        assertTrue(exception.getMessage().contains("Invalid phone number"));
    }
}