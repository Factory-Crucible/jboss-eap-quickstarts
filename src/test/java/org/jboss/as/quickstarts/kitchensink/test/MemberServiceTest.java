package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Event<Member> memberEventSrc;

    private MemberRegistration memberRegistration;

    @BeforeEach
    public void setup() {
        memberRegistration = new MemberRegistration();
        memberRegistration.setMemberRepository(memberRepository);
        memberRegistration.setMemberEventSrc(memberEventSrc);
    }

    @Test
    public void testRegister() throws Exception {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        memberRegistration.register(member);

        verify(memberRepository).create(member);
        verify(memberEventSrc).fire(member);
    }

    @Test
    public void testRegisterInvalidMember() {
        Member member = new Member();
        // Invalid email
        member.setName("Jane Doe");
        member.setEmail("invalid-email");
        member.setPhoneNumber("1234567890");

        assertThrows(Exception.class, () -> memberRegistration.register(member));
    }

    @Test
    public void testFindAllMembers() {
        List<Member> expectedMembers = new ArrayList<>();
        expectedMembers.add(new Member("John Doe", "john@example.com", "1234567890"));
        expectedMembers.add(new Member("Jane Doe", "jane@example.com", "0987654321"));

        when(memberRepository.findAllOrderedByName()).thenReturn(expectedMembers);

        List<Member> actualMembers = memberRepository.findAllOrderedByName();

        assertEquals(expectedMembers.size(), actualMembers.size());
        assertEquals(expectedMembers.get(0).getName(), actualMembers.get(0).getName());
        assertEquals(expectedMembers.get(1).getName(), actualMembers.get(1).getName());
    }

    @Test
    public void testFindMemberById() {
        Member expectedMember = new Member("John Doe", "john@example.com", "1234567890");
        expectedMember.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(expectedMember);

        Member actualMember = memberRepository.findById(1L);

        assertNotNull(actualMember);
        assertEquals(expectedMember.getId(), actualMember.getId());
        assertEquals(expectedMember.getName(), actualMember.getName());
        assertEquals(expectedMember.getEmail(), actualMember.getEmail());
    }
}