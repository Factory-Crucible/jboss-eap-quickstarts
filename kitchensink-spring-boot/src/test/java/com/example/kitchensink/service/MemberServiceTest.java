package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link MemberService}.
 * These tests verify that the service methods work as expected.
 */
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member john;
    private Member jane;
    private Member bob;

    @BeforeEach
    public void setUp() {
        // Create test members
        john = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@mailinator.com")
                .phoneNumber("2125551212")
                .build();

        jane = Member.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane.doe@mailinator.com")
                .phoneNumber("2125552323")
                .build();

        bob = Member.builder()
                .id(3L)
                .name("Bob Johnson")
                .email("bob.johnson@mailinator.com")
                .phoneNumber("2125553434")
                .build();
    }

    @Test
    public void testFindAllMembers() {
        // Given
        List<Member> members = Arrays.asList(john, jane, bob);
        when(memberRepository.findAll()).thenReturn(members);

        // When
        List<Member> result = memberService.findAllMembers();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(john, jane, bob);
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllMembersOrderedByName() {
        // Given
        List<Member> members = Arrays.asList(bob, jane, john); // Ordered by name
        when(memberRepository.findAllOrderedByName()).thenReturn(members);

        // When
        List<Member> result = memberService.findAllMembersOrderedByName();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(bob, jane, john);
        verify(memberRepository, times(1)).findAllOrderedByName();
    }

    @Test
    public void testFindMemberById() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(john));

        // When
        Optional<Member> result = memberService.findMemberById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(john);
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindMemberById_NotFound() {
        // Given
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Member> result = memberService.findMemberById(99L);

        // Then
        assertThat(result).isEmpty();
        verify(memberRepository, times(1)).findById(99L);
    }

    @Test
    public void testFindMemberByEmail() {
        // Given
        when(memberRepository.findByEmail("john.smith@mailinator.com")).thenReturn(Optional.of(john));

        // When
        Optional<Member> result = memberService.findMemberByEmail("john.smith@mailinator.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(john);
        verify(memberRepository, times(1)).findByEmail("john.smith@mailinator.com");
    }

    @Test
    public void testFindMemberByEmail_NotFound() {
        // Given
        when(memberRepository.findByEmail("nonexistent@mailinator.com")).thenReturn(Optional.empty());

        // When
        Optional<Member> result = memberService.findMemberByEmail("nonexistent@mailinator.com");

        // Then
        assertThat(result).isEmpty();
        verify(memberRepository, times(1)).findByEmail("nonexistent@mailinator.com");
    }

    @Test
    public void testCreateMember() {
        // Given
        Member newMember = Member.builder()
                .name("Alice Williams")
                .email("alice.williams@mailinator.com")
                .phoneNumber("2125554545")
                .build();

        Member savedMember = Member.builder()
                .id(4L)
                .name("Alice Williams")
                .email("alice.williams@mailinator.com")
                .phoneNumber("2125554545")
                .build();

        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // When
        Member result = memberService.createMember(newMember);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("Alice Williams");
        assertThat(result.getEmail()).isEqualTo("alice.williams@mailinator.com");
        assertThat(result.getPhoneNumber()).isEqualTo("2125554545");
        
        verify(memberRepository, times(1)).existsByEmail("alice.williams@mailinator.com");
        verify(memberRepository, times(1)).save(newMember);
    }

    @Test
    public void testCreateMember_EmailAlreadyExists() {
        // Given
        Member newMember = Member.builder()
                .name("John Clone")
                .email("john.smith@mailinator.com") // Email already exists
                .phoneNumber("2125556767")
                .build();

        when(memberRepository.existsByEmail("john.smith@mailinator.com")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(newMember);
        });
        
        verify(memberRepository, times(1)).existsByEmail("john.smith@mailinator.com");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testUpdateMember() {
        // Given
        Member updatedMember = Member.builder()
                .name("John Smith Updated")
                .email("john.updated@mailinator.com")
                .phoneNumber("2125559999")
                .build();

        Member existingMember = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@mailinator.com")
                .phoneNumber("2125551212")
                .build();

        Member savedMember = Member.builder()
                .id(1L)
                .name("John Smith Updated")
                .email("john.updated@mailinator.com")
                .phoneNumber("2125559999")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.findByEmail("john.updated@mailinator.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // When
        Member result = memberService.updateMember(1L, updatedMember);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Smith Updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@mailinator.com");
        assertThat(result.getPhoneNumber()).isEqualTo("2125559999");
        
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).findByEmail("john.updated@mailinator.com");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    public void testUpdateMember_NotFound() {
        // Given
        Member updatedMember = Member.builder()
                .name("Nonexistent User")
                .email("nonexistent@mailinator.com")
                .phoneNumber("2125551111")
                .build();

        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateMember(99L, updatedMember);
        });
        
        verify(memberRepository, times(1)).findById(99L);
        verify(memberRepository, never()).findByEmail(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testUpdateMember_EmailAlreadyInUse() {
        // Given
        Member updatedMember = Member.builder()
                .name("John Smith Updated")
                .email("jane.doe@mailinator.com") // Email already used by Jane
                .phoneNumber("2125559999")
                .build();

        Member existingMember = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@mailinator.com")
                .phoneNumber("2125551212")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.findByEmail("jane.doe@mailinator.com")).thenReturn(Optional.of(jane));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateMember(1L, updatedMember);
        });
        
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).findByEmail("jane.doe@mailinator.com");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testDeleteMember() {
        // Given
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        // When
        memberService.deleteMember(1L);

        // Then
        verify(memberRepository, times(1)).existsById(1L);
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteMember_NotFound() {
        // Given
        when(memberRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.deleteMember(99L);
        });
        
        verify(memberRepository, times(1)).existsById(99L);
        verify(memberRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testEmailExists() {
        // Given
        when(memberRepository.existsByEmail("john.smith@mailinator.com")).thenReturn(true);
        when(memberRepository.existsByEmail("nonexistent@mailinator.com")).thenReturn(false);

        // When
        boolean existsResult = memberService.emailExists("john.smith@mailinator.com");
        boolean notExistsResult = memberService.emailExists("nonexistent@mailinator.com");

        // Then
        assertThat(existsResult).isTrue();
        assertThat(notExistsResult).isFalse();
        
        verify(memberRepository, times(1)).existsByEmail("john.smith@mailinator.com");
        verify(memberRepository, times(1)).existsByEmail("nonexistent@mailinator.com");
    }
}
