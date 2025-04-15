package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Service for member registration operations.
 * Migrated from JBoss EAP kitchensink quickstart MemberRegistration EJB.
 */
@Service
@Validated
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new member
     *
     * @param member the member to register
     * @return the registered member with ID
     * @throws MemberRegistrationException if registration fails
     */
    @Transactional
    public Member register(@NotNull @Valid Member member) throws MemberRegistrationException {
        log.info("Registering member: {}", member.getName());
        
        try {
            // Check if member with email already exists
            if (memberRepository.existsByEmail(member.getEmail())) {
                throw new MemberRegistrationException("Email already exists: " + member.getEmail());
            }
            
            // Save the member
            Member savedMember = memberRepository.save(member);
            
            // Publish event that member has been created
            eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
            
            log.info("Successfully registered member: {} with ID: {}", 
                     savedMember.getName(), savedMember.getId());
            
            return savedMember;
        } catch (Exception e) {
            if (e instanceof MemberRegistrationException) {
                throw e;
            }
            log.error("Error registering member: {}", member.getName(), e);
            throw new MemberRegistrationException("Error registering member: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find a member by ID
     * 
     * @param id the member ID
     * @return the member or null if not found
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }
    
    /**
     * Find all members ordered by name
     * 
     * @return list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public Iterable<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Custom exception for member registration errors
     */
    public static class MemberRegistrationException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public MemberRegistrationException(String message) {
            super(message);
        }
        
        public MemberRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Event published when a member is registered
     */
    public static class MemberRegisteredEvent {
        private final Member member;
        
        public MemberRegisteredEvent(Member member) {
            this.member = member;
        }
        
        public Member getMember() {
            return member;
        }
    }
}
