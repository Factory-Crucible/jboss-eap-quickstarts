package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.persistence.EntityNotFoundException;
import org.jboss.as.quickstarts.kitchensink.exception.UniqueEmailException;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MemberService class.
 * Tests the service methods using mocked repository and event publisher.
 */
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberService memberService;

    @Captor
    private ArgumentCaptor<Member> memberCaptor;

    private Member john;
    private Member alice;
    private Member bob;
    private List<Member> memberList;

    @BeforeEach
    public void setup() {
        // Create test members
        john = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .build();

        alice = Member.builder()
                .id(2L)
                .name("Alice Johnson")
                .email("alice@example.com")
                .phoneNumber("2345678901")
                .build();

        bob = Member.builder()
                .id(3L)
                .name("Bob Williams")
                .email("bob@example.com")
                .phoneNumber("3456789012")
                .build();

        memberList = Arrays.asList(alice, bob, john);
    }

    @Test
    public void testRegister_WhenEmailIsUnique_ShouldRegisterMember() {
        // Given
        Member newMember = Member.builder()
                .name("Emma Wilson")
                .email("emma@example.com")
                .phoneNumber("4567890123")
                .build();

        Member savedMember = Member.builder()
                .id(4L)
                .name("Emma Wilson")
                .email("emma@example.com")
                .phoneNumber("4567890123")
                .build();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // When
        Member result = memberService.register(newMember);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("Emma Wilson");

        verify(memberRepository).findByEmail("emma@example.com");
        verify(memberRepository).save(newMember);
        verify(eventPublisher).publishEvent(savedMember);
    }

    @Test
    public void testRegister_WhenEmailExists_ShouldThrowUniqueEmailException() {
        // Given
        Member newMember = Member.builder()
                .name("Another John")
                .email("john@example.com") // Same email as john
                .phoneNumber("5678901234")
                .build();

        when(memberRepository.findByEmail("john@example.com")).thenReturn(Optional.of(john));

        // When/Then
        assertThrows(UniqueEmailException.class, () -> memberService.register(newMember));
        verify(memberRepository).findByEmail("john@example.com");
        verify(memberRepository, never()).save(any(Member.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    public void testFindById_WhenIdExists_ShouldReturnMember() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(john));

        // When
        Member result = memberService.findById(1L);

        // Then
        assertThat(result).isEqualTo(john);
        verify(memberRepository).findById(1L);
    }

    @Test
    public void testFindById_WhenIdDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> memberService.findById(999L));
        verify(memberRepository).findById(999L);
    }

    @Test
    public void testFindByEmail_WhenEmailExists_ShouldReturnMember() {
        // Given
        when(memberRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(alice));

        // When
        Member result = memberService.findByEmail("alice@example.com");

        // Then
        assertThat(result).isEqualTo(alice);
        verify(memberRepository).findByEmail("alice@example.com");
    }

    @Test
    public void testFindByEmail_WhenEmailDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        when(memberRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> memberService.findByEmail("nonexistent@example.com"));
        verify(memberRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testFindAllOrderedByName_ShouldReturnAllMembers() {
        // Given
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(memberList);

        // When
        List<Member> result = memberService.findAllOrderedByName();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(alice, bob, john);
        verify(memberRepository).findAllByOrderByNameAsc();
    }

    @Test
    public void testEmailExists_WhenEmailExists_ShouldReturnTrue() {
        // Given
        when(memberRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(bob));

        // When
        boolean result = memberService.emailExists("bob@example.com");

        // Then
        assertThat(result).isTrue();
        verify(memberRepository).findByEmail("bob@example.com");
    }

    @Test
    public void testEmailExists_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // Given
        when(memberRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        boolean result = memberService.emailExists("nonexistent@example.com");

        // Then
        assertThat(result).isFalse();
        verify(memberRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testUpdateMember_WhenMemberExistsAndEmailUnchanged_ShouldUpdateMember() {
        // Given
        Member updatedJohn = Member.builder()
                .id(1L)
                .name("John Smith Updated")
                .email("john@example.com") // Same email
                .phoneNumber("9876543210")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(john));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedJohn);

        // When
        Member result = memberService.updateMember(1L, updatedJohn);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Smith Updated");
        assertThat(result.getPhoneNumber()).isEqualTo("9876543210");

        verify(memberRepository).findById(1L);
        verify(memberRepository).save(memberCaptor.capture());
        
        Member capturedMember = memberCaptor.getValue();
        assertThat(capturedMember.getName()).isEqualTo("John Smith Updated");
        assertThat(capturedMember.getPhoneNumber()).isEqualTo("9876543210");
    }

    @Test
    public void testUpdateMember_WhenMemberExistsAndEmailChanged_ShouldUpdateMember() {
        // Given
        Member updatedJohn = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("john.updated@example.com") // Changed email
                .phoneNumber("1234567890")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(john));
        when(memberRepository.findByEmail("john.updated@example.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(updatedJohn);

        // When
        Member result = memberService.updateMember(1L, updatedJohn);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");

        verify(memberRepository).findById(1L);
        verify(memberRepository).findByEmail("john.updated@example.com");
        verify(memberRepository).save(memberCaptor.capture());
        
        Member capturedMember = memberCaptor.getValue();
        assertThat(capturedMember.getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    public void testUpdateMember_WhenMemberExistsButNewEmailTaken_ShouldThrowUniqueEmailException() {
        // Given
        Member updatedJohn = Member.builder()
                .id(1L)
                .name("John Smith")
                .email("alice@example.com") // Alice's email
                .phoneNumber("1234567890")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(john));
        when(memberRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(alice));

        // When/Then
        assertThrows(UniqueEmailException.class, () -> memberService.updateMember(1L, updatedJohn));
        
        verify(memberRepository).findById(1L);
        verify(memberRepository).findByEmail("alice@example.com");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testUpdateMember_WhenMemberDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        Member updatedMember = Member.builder()
                .id(999L)
                .name("Nonexistent Member")
                .email("nonexistent@example.com")
                .phoneNumber("1234567890")
                .build();

        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> memberService.updateMember(999L, updatedMember));
        
        verify(memberRepository).findById(999L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    public void testDeleteMember_WhenMemberExists_ShouldDeleteMember() {
        // Given
        when(memberRepository.findById(2L)).thenReturn(Optional.of(alice));
        doNothing().when(memberRepository).delete(alice);

        // When
        memberService.deleteMember(2L);

        // Then
        verify(memberRepository).findById(2L);
        verify(memberRepository).delete(alice);
    }

    @Test
    public void testDeleteMember_WhenMemberDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> memberService.deleteMember(999L));
        
        verify(memberRepository).findById(999L);
        verify(memberRepository, never()).delete(any(Member.class));
    }
}
