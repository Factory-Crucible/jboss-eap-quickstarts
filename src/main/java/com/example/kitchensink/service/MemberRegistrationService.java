package com.example.kitchensink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.event.MemberRegisteredEvent;

/**
 * Service for member registration operations.
 * This service handles the business logic for registering new members.
 */
@Service
public class MemberRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(MemberRegistrationService.class);
    
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public MemberRegistrationService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Register a new member.
     * This method persists the member to the database and publishes an event.
     *
     * @param member The member to register
     * @throws Exception If registration fails
     */
    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new Exception("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        memberRepository.save(member);
        
        // Publish an event to notify other components
        eventPublisher.publishEvent(new MemberRegisteredEvent(member));
    }
}
