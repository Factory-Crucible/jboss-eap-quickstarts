package com.example.kitchensink.service;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the MemberService interface.
 * 
 * This class provides the business logic for managing Member entities,
 * including registration, retrieval, and search functionality.
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructs a new MemberServiceImpl with the required dependencies.
     * 
     * @param memberRepository Repository for Member entities
     * @param eventPublisher Publisher for application events
     */
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation checks if a member with the same email already exists,
     * saves the new member to the database, and publishes a MemberRegisteredEvent.
     */
    @Override
    @Transactional
    public Member register(Member member) throws DuplicateResourceException {
        log.info("Registering new member: {} ({})", member.getName(), member.getEmail());
        
        // Check if a member with the same email already exists
        if (emailExists(member.getEmail())) {
            log.error("Registration failed: Email {} already exists", member.getEmail());
            throw new DuplicateResourceException("Email already exists: " + member.getEmail());
        }
        
        // Save the member to the database
        Member savedMember = memberRepository.save(member);
        log.info("Member registered successfully with ID: {}", savedMember.getId());
        
        // Publish the member registered event
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        return savedMember;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation retrieves all members ordered by name from the repository.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        log.debug("Retrieving all members ordered by name");
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation retrieves a member by ID from the repository,
     * throwing a ResourceNotFoundException if not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Member findById(Long id) throws ResourceNotFoundException {
        log.debug("Finding member by ID: {}", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Member not found with ID: {}", id);
                    return new ResourceNotFoundException("Member not found with ID: " + id);
                });
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation retrieves a member by email from the repository,
     * throwing a ResourceNotFoundException if not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Member findByEmail(String email) throws ResourceNotFoundException {
        log.debug("Finding member by email: {}", email);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Member not found with email: {}", email);
                    return new ResourceNotFoundException("Member not found with email: " + email);
                });
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation checks if a member with the given email exists in the repository.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        log.debug("Checking if email exists: {}", email);
        return memberRepository.findByEmail(email).isPresent();
    }
}
