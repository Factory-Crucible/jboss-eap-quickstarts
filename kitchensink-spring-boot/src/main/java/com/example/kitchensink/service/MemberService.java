package com.example.kitchensink.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;

/**
 * Service for member registration and management.
 * This class replaces the MemberRegistration EJB from the JBoss application.
 * 
 * It uses Spring's @Service annotation instead of EJB's @Stateless,
 * and Spring's transaction management instead of EJB's container-managed transactions.
 */
@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Creates a new MemberService with the given dependencies.
     * This constructor uses constructor injection, which is the preferred
     * approach in Spring Boot applications.
     * 
     * @param memberRepository repository for member data access
     * @param eventPublisher publisher for application events
     */
    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Registers a new member.
     * This method validates that the email is not already in use,
     * persists the member, and publishes an event.
     * 
     * @param member the member to register
     * @return the registered member with ID assigned
     * @throws IllegalArgumentException if the email is already in use
     */
    @Transactional
    public Member register(Member member) {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish event (equivalent to CDI event.fire())
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        return savedMember;
    }
    
    /**
     * Gets all members ordered by name.
     * 
     * @return list of all members
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Gets a member by ID.
     * 
     * @param id the member ID
     * @return an Optional containing the member if found, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
    
    /**
     * Checks if a member with the given email exists.
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
