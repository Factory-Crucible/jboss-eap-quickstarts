package com.factory.kitchensink.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.dto.MemberDTO;
import com.factory.kitchensink.event.MemberRegisteredEvent;
import com.factory.kitchensink.exception.EmailAlreadyExistsException;
import com.factory.kitchensink.exception.MemberNotFoundException;
import com.factory.kitchensink.mapper.MemberMapper;
import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import com.factory.kitchensink.service.impl.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private MemberMapper memberMapper;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private MemberServiceImpl memberService;
    
    private Member testMember;
    private MemberDTO testMemberDTO;
    private CreateMemberDTO createMemberDTO;
    
    @BeforeEach
    void setUp() {
        // Set up test data
        testMember = new Member(1L, "John Doe", "john.doe@example.com", "1234567890");
        testMemberDTO = new MemberDTO(1L, "John Doe", "john.doe@example.com", "1234567890");
        createMemberDTO = new CreateMemberDTO("John Doe", "john.doe@example.com", "1234567890");
    }
    
    @Test
    void register_ShouldRegisterNewMember_WhenEmailDoesNotExist() {
        // Arrange
        when(memberRepository.findByEmail(createMemberDTO.getEmail())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(createMemberDTO)).thenReturn(testMember);
        when(memberRepository.save(testMember)).thenReturn(testMember);
        when(memberMapper.toDTO(testMember)).thenReturn(testMemberDTO);
        
        // Act
        MemberDTO result = memberService.register(createMemberDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(testMemberDTO.getId(), result.getId());
        assertEquals(testMemberDTO.getName(), result.getName());
        assertEquals(testMemberDTO.getEmail(), result.getEmail());
        assertEquals(testMemberDTO.getPhoneNumber(), result.getPhoneNumber());
        
        // Verify interactions
        verify(memberRepository).findByEmail(createMemberDTO.getEmail());
        verify(memberMapper).toEntity(createMemberDTO);
        verify(memberRepository).save(testMember);
        verify(memberMapper).toDTO(testMember);
        verify(eventPublisher).publishEvent(any(MemberRegisteredEvent.class));
    }
    
    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(memberRepository.findByEmail(createMemberDTO.getEmail())).thenReturn(Optional.of(testMember));
        
        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            memberService.register(createMemberDTO);
        });
        
        // Verify interactions
        verify(memberRepository).findByEmail(createMemberDTO.getEmail());
        verify(memberMapper, never()).toEntity(any(CreateMemberDTO.class));
        verify(memberRepository, never()).save(any(Member.class));
        verify(eventPublisher, never()).publishEvent(any(MemberRegisteredEvent.class));
    }
    
    @Test
    void findAllMembers_ShouldReturnAllMembers() {
        // Arrange
        List<Member> members = Arrays.asList(
            testMember,
            new Member(2L, "Jane Doe", "jane.doe@example.com", "0987654321")
        );
        
        List<MemberDTO> memberDTOs = Arrays.asList(
            testMemberDTO,
            new MemberDTO(2L, "Jane Doe", "jane.doe@example.com", "0987654321")
        );
        
        when(memberRepository.findAll()).thenReturn(members);
        when(memberMapper.toDTO(members.get(0))).thenReturn(memberDTOs.get(0));
        when(memberMapper.toDTO(members.get(1))).thenReturn(memberDTOs.get(1));
        
        // Act
        List<MemberDTO> result = memberService.findAllMembers();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(memberDTOs.get(0).getId(), result.get(0).getId());
        assertEquals(memberDTOs.get(1).getId(), result.get(1).getId());
        
        // Verify interactions
        verify(memberRepository).findAll();
        verify(memberMapper, times(2)).toDTO(any(Member.class));
    }
    
    @Test
    void findById_ShouldReturnMember_WhenMemberExists() {
        // Arrange
        Long id = 1L;
        when(memberRepository.findById(id)).thenReturn(Optional.of(testMember));
        when(memberMapper.toDTO(testMember)).thenReturn(testMemberDTO);
        
        // Act
        MemberDTO result = memberService.findById(id);
        
        // Assert
        assertNotNull(result);
        assertEquals(testMemberDTO.getId(), result.getId());
        assertEquals(testMemberDTO.getName(), result.getName());
        
        // Verify interactions
        verify(memberRepository).findById(id);
        verify(memberMapper).toDTO(testMember);
    }
    
    @Test
    void findById_ShouldThrowException_WhenMemberDoesNotExist() {
        // Arrange
        Long id = 999L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.findById(id);
        });
        
        // Verify interactions
        verify(memberRepository).findById(id);
        verify(memberMapper, never()).toDTO(any(Member.class));
    }
    
    @Test
    void findByEmail_ShouldReturnMember_WhenMemberExists() {
        // Arrange
        String email = "john.doe@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(testMember));
        when(memberMapper.toDTO(testMember)).thenReturn(testMemberDTO);
        
        // Act
        MemberDTO result = memberService.findByEmail(email);
        
        // Assert
        assertNotNull(result);
        assertEquals(testMemberDTO.getEmail(), result.getEmail());
        
        // Verify interactions
        verify(memberRepository).findByEmail(email);
        verify(memberMapper).toDTO(testMember);
    }
    
    @Test
    void findByEmail_ShouldThrowException_WhenMemberDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.findByEmail(email);
        });
        
        // Verify interactions
        verify(memberRepository).findByEmail(email);
        verify(memberMapper, never()).toDTO(any(Member.class));
    }
}
