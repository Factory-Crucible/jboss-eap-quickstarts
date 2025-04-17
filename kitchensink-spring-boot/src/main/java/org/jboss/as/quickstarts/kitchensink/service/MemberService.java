package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Member entities.
 * Handles business logic for member registration and retrieval.
 * Replaces the functionality of the original MemberRegistration EJB.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Registers a new member.
     * Validates the member, checks for duplicate email, and persists the member.
     * Fires an event after successful registration.
     *
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws IllegalArgumentException if member validation fails
     */
    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering member: {}", member.getName());
        
        // Check if email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Fire an event (similar to the CDI event in the original application)
        eventPublisher.publishEvent(savedMember);
        
        log.info("Successfully registered member: {} with id: {}", savedMember.getName(), savedMember.getId());
        return savedMember;
    }

    /**
     * Finds a member by ID.
     *
     * @param id The member ID
     * @return Optional containing the member if found
     */
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Finds a member by email.
     *
     * @param email The email address
     * @return Optional containing the member if found
     */
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Gets all members ordered by name.
     *
     * @return List of all members ordered by name
     */
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }
}
