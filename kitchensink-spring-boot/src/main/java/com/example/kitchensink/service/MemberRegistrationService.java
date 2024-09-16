package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

/**
 * Spring Service for Member registration.
 * This class replaces the JBoss MemberRegistration stateless EJB.
 *
 * Changes from JBoss version:
 * - Uses @Service instead of @Stateless
 * - Uses Spring Data JPA's MemberRepository instead of EntityManager
 * - Uses @Autowired instead of @Inject
 * - Uses ApplicationEventPublisher instead of Event<Member>
 * - Transaction management is handled by Spring's @Transactional
 */
@Service
public class MemberRegistrationService {

    private static final Logger log = Logger.getLogger(MemberRegistrationService.class.getName());

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Registers a new member.
     *
     * @param member The member to register
     * @throws Exception if there's an error during registration
     */
    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        memberRepository.save(member);
        eventPublisher.publishEvent(member);
    }
}