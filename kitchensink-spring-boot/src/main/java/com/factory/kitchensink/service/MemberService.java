package com.factory.kitchensink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import com.factory.kitchensink.service.event.MemberRegisteredEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing member registration and retrieval.
 * Replaces the functionality of MemberRegistration from the JBoss application.
 */
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new member.
     * 
     * @param member the member to register
     * @return the registered member with ID assigned
     * @throws Exception if registration fails
     */
    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new Exception("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish an event to notify other components
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        return savedMember;
    }

    /**
     * Retrieves all members ordered by name.
     * 
     * @return list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }

    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return the member, or null if not found
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    /**
     * Finds a member by email.
     * 
     * @param email the email address
     * @return the member, or null if not found
     */
    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
