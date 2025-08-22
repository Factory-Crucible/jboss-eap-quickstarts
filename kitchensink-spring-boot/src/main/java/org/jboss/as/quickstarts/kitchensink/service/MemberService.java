package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring Service that replaces the original EJB MemberRegistration.
 * Handles member registration with proper transaction management.
 */
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    /**
     * Explicit constructor for dependency injection.
     *
     * @param memberRepository  repository to manage members
     * @param eventPublisher    Spring application event publisher
     */
    public MemberService(MemberRepository memberRepository,
                         ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new member
     *
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws Exception if registration fails
     */
    public Member register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        try {
            // Check if member with this email already exists
            if (memberRepository.findByEmail(member.getEmail()) != null) {
                throw new Exception("Email already exists: " + member.getEmail());
            }
            
            // Save the member
            Member savedMember = memberRepository.save(member);
            
            // Publish event (equivalent to CDI event.fire())
            eventPublisher.publishEvent(savedMember);
            
            return savedMember;
        } catch (Exception e) {
            log.error("Error registering member: {}", e.getMessage(), e);
            throw e;
        }
    }
}
