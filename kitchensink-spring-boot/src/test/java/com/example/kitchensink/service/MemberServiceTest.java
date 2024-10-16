package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    void testRegisterMember() {
        Member member = new Member(null, "John Doe", "john@example.com", "1234567890");
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member registeredMember = memberService.registerMember(member);

        assertNotNull(registeredMember);
        assertEquals(member.getName(), registeredMember.getName());
        assertEquals(member.getEmail(), registeredMember.getEmail());
        verify(memberRepository, times(1)).findByEmail(member.getEmail());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testRegisterMemberWithExistingEmail() {
        Member existingMember = new Member(1L, "Jane Doe", "jane@example.com", "0987654321");
        when(memberRepository.findByEmail(existingMember.getEmail())).thenReturn(Optional.of(existingMember));

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.registerMember(existingMember);
        });

        verify(memberRepository, times(1)).findByEmail(existingMember.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testGetAllMembers() {
        List<Member> expectedMembers = Arrays.asList(
            new Member(1L, "John Doe", "john@example.com", "1234567890"),
            new Member(2L, "Jane Doe", "jane@example.com", "0987654321")
        );
        when(memberRepository.findAll()).thenReturn(expectedMembers);

        List<Member> actualMembers = memberService.getAllMembers();

        assertEquals(expectedMembers.size(), actualMembers.size());
        assertEquals(expectedMembers, actualMembers);
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void testFindMemberByEmail() {
        String email = "john@example.com";
        Member expectedMember = new Member(1L, "John Doe", email, "1234567890");
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(expectedMember));

        Optional<Member> actualMember = memberService.findMemberByEmail(email);

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember, actualMember.get());
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindMemberByEmailNotFound() {
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<Member> actualMember = memberService.findMemberByEmail(email);

        assertFalse(actualMember.isPresent());
        verify(memberRepository, times(1)).findByEmail(email);
    }
}