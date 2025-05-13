package com.factory.kitchensink.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.event.MemberRegisteredEvent;
import com.factory.kitchensink.dto.MemberDTO;
import com.factory.kitchensink.exception.EmailAlreadyExistsException;
import com.factory.kitchensink.exception.MemberNotFoundException;
import com.factory.kitchensink.mapper.MemberMapper;
import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import com.factory.kitchensink.service.MemberService;

/**
 * Implementation of the MemberService interface.
 * Provides business logic for member-related operations.
 */
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Constructor-based dependency injection.
     * 
     * @param memberRepository repository for member data access
     * @param memberMapper mapper for converting between entities and DTOs
     */
    public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemberDTO register(CreateMemberDTO memberDTO) {
        // Check if email already exists
        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + memberDTO.getEmail());
        }
        
        // Convert DTO to entity
        Member member = memberMapper.toEntity(memberDTO);
        
        // Save entity
        member = memberRepository.save(member);
        
        // Publish event
        eventPublisher.publishEvent(new MemberRegisteredEvent(member));
        
        // Return DTO
        return memberMapper.toDTO(member);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(memberMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public MemberDTO findById(Long id) {
        return memberRepository.findById(id)
                .map(memberMapper::toDTO)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public MemberDTO findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(memberMapper::toDTO)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with email: " + email));
    }
}
