package com.example.kitchensink.service.impl;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link MemberService} interface.
 * This class provides the business logic for member operations.
 */
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    /**
     * Constructor for dependency injection.
     *
     * @param memberRepository the repository for member data access
     * @param eventPublisher the event publisher for publishing domain events
     */
    public MemberServiceImpl(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findAll() {
        logger.debug("Finding all members");
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        logger.debug("Finding member with id: {}", id);
        return memberRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        logger.debug("Finding member with email: {}", email);
        return memberRepository.findByEmail(email);
    }

    @Override
    public Member register(Member member) {
        logger.info("Registering new member: {}", member.getName());
        
        // Check if email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            logger.warn("Registration failed. Email already exists: {}", member.getEmail());
            throw new DuplicateResourceException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish member registered event
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        logger.info("Member registered successfully with id: {}", savedMember.getId());
        return savedMember;
    }

    @Override
    public Member update(Long id, Member memberDetails) {
        logger.info("Updating member with id: {}", id);
        
        // Find the member by id
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed. Member not found with id: {}", id);
                    return new ResourceNotFoundException("Member not found with id: " + id);
                });
        
        // Check if email is being changed and if it already exists
        if (!existingMember.getEmail().equals(memberDetails.getEmail()) && 
                memberRepository.existsByEmail(memberDetails.getEmail())) {
            logger.warn("Update failed. Email already exists: {}", memberDetails.getEmail());
            throw new DuplicateResourceException("Email already exists: " + memberDetails.getEmail());
        }
        
        // Update member details
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        
        // Save the updated member
        Member updatedMember = memberRepository.save(existingMember);
        
        logger.info("Member updated successfully: {}", updatedMember.getName());
        return updatedMember;
    }

    @Override
    public boolean delete(Long id) {
        logger.info("Deleting member with id: {}", id);
        
        // Check if member exists
        if (!memberRepository.existsById(id)) {
            logger.warn("Delete failed. Member not found with id: {}", id);
            return false;
        }
        
        // Delete the member
        memberRepository.deleteById(id);
        
        logger.info("Member deleted successfully with id: {}", id);
        return true;
    }
}
