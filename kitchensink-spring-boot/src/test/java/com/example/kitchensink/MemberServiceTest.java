package com.example.kitchensink;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MemberServiceTest {

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

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
    void testRegister() throws Exception {
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        Member registeredMember = memberService.register(testMember);

        assertNotNull(registeredMember);
        assertEquals(testMember.getName(), registeredMember.getName());
        assertEquals(testMember.getEmail(), registeredMember.getEmail());
        verify(memberRepository).findByEmail(testMember.getEmail());
        verify(memberRepository).save(testMember);
    }

    @Test
    void testRegisterWithExistingEmail() {
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.of(testMember));

        assertThrows(Exception.class, () -> memberService.register(testMember));
        verify(memberRepository).findByEmail(testMember.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testGetAllMembers() {
        List<Member> members = Arrays.asList(testMember, new Member());
        when(memberRepository.findAll()).thenReturn(members);

        List<Member> result = memberService.getAllMembers();

        assertEquals(2, result.size());
        verify(memberRepository).findAll();
    }

    @Test
    void testGetMemberById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        Optional<Member> result = memberService.getMemberById(1L);

        assertTrue(result.isPresent());
        assertEquals(testMember.getName(), result.get().getName());
        verify(memberRepository).findById(1L);
    }

    @Test
    void testUpdateMember() throws Exception {
        when(memberRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        Member updatedMember = memberService.updateMember(testMember);

        assertNotNull(updatedMember);
        assertEquals(testMember.getName(), updatedMember.getName());
        verify(memberRepository).existsById(1L);
        verify(memberRepository).save(testMember);
    }

    @Test
    void testUpdateNonExistentMember() {
        when(memberRepository.existsById(1L)).thenReturn(false);

        assertThrows(Exception.class, () -> memberService.updateMember(testMember));
        verify(memberRepository).existsById(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testDeleteMember() throws Exception {
        when(memberRepository.existsById(1L)).thenReturn(true);

        memberService.deleteMember(1L);

        verify(memberRepository).existsById(1L);
        verify(memberRepository).deleteById(1L);
    }

    @Test
    void testDeleteNonExistentMember() {
        when(memberRepository.existsById(1L)).thenReturn(false);

        assertThrows(Exception.class, () -> memberService.deleteMember(1L));
        verify(memberRepository).existsById(1L);
        verify(memberRepository, never()).deleteById(anyLong());
    }
}