package com.example.kitchensink;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MemberRegistrationServiceTest {

    @Autowired
    private MemberRegistrationService memberRegistrationService;

    @MockBean
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setName("John Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    void testRegisterMemberSuccess() {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        Member registeredMember = memberRegistrationService.register(testMember);

        assertNotNull(registeredMember);
        assertEquals(testMember.getName(), registeredMember.getName());
        assertEquals(testMember.getEmail(), registeredMember.getEmail());
        assertEquals(testMember.getPhoneNumber(), registeredMember.getPhoneNumber());

        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testRegisterMemberWithExistingEmail() {
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(testMember);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.register(testMember);
        });

        String expectedMessage = "Email already exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testRegisterMemberWithInvalidData() {
        Member invalidMember = new Member();
        // Leave all fields empty to trigger validation errors

        Exception exception = assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.register(invalidMember);
        });

        String expectedMessage = "Invalid member data";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(memberRepository, never()).save(any(Member.class));
    }
}