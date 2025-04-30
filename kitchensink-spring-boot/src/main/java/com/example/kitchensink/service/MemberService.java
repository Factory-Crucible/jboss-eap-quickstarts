package com.example.kitchensink.service;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.exception.MemberAlreadyExistsException;
import com.example.kitchensink.exception.MemberNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Service that handles business logic for Member operations.
 * This service replaces the functionality of MemberRegistration.java
 * from the original JBoss application.
 */
@Service
@Validated
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new member.
     * 
     * @param member the member to register
     * @return the registered member with ID assigned
     * @throws MemberAlreadyExistsException if a member with the same email already exists
     */
    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering member: {}", member.getName());
        
        // Check if member with same email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new MemberAlreadyExistsException("Member with email " + member.getEmail() + " already exists");
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish event that a new member has been registered
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        return savedMember;
    }

    /**
     * Find a member by ID.
     * 
     * @param id the ID of the member to find
     * @return the found member
     * @throws MemberNotFoundException if no member with the given ID exists
     */
    @Transactional(readOnly = true)
    public Member findById(@NotNull Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));
    }

    /**
     * Find a member by email.
     * 
     * @param email the email of the member to find
     * @return the found member
     * @throws MemberNotFoundException if no member with the given email exists
     */
    @Transactional(readOnly = true)
    public Member findByEmail(@NotNull @Email String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("Member with email " + email + " not found"));
    }

    /**
     * Find all members ordered by name.
     * 
     * @return list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(@NotNull @Email String email) {
        return memberRepository.existsByEmail(email);
    }
}
