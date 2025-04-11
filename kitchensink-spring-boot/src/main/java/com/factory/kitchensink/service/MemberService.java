package com.factory.kitchensink.service;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing {@link Member} entities.
 * This class handles all business logic related to members including
 * validation, persistence, and event publishing.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final Validator validator;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Registers a new member in the system.
     * Validates the member, checks for duplicate email, persists the member,
     * and publishes a member registration event.
     *
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws ConstraintViolationException if validation fails
     * @throws IllegalArgumentException if a member with the same email already exists
     */
    public Member register(Member member) {
        log.info("Registering member: {}", member.getName());
        
        // Validate member using Bean Validation
        validateMember(member);
        
        // Check if email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            log.error("Email already exists: {}", member.getEmail());
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        
        // Persist the member
        Member savedMember = memberRepository.save(member);
        
        // Publish member registered event
        eventPublisher.publishEvent(savedMember);
        log.info("Successfully registered member: {} with ID: {}", savedMember.getName(), savedMember.getId());
        
        return savedMember;
    }

    /**
     * Retrieves all members ordered by name.
     *
     * @return a list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Finds a member by their ID.
     *
     * @param id the ID of the member to find
     * @return an Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Finds a member by their email address.
     *
     * @param email the email of the member to find
     * @return an Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Updates an existing member.
     * Validates the member, checks that the member exists,
     * and publishes a member updated event.
     *
     * @param id the ID of the member to update
     * @param memberDetails the updated member details
     * @return the updated member
     * @throws ConstraintViolationException if validation fails
     * @throws IllegalArgumentException if the member doesn't exist or email is taken by another member
     */
    public Member updateMember(Long id, Member memberDetails) {
        log.info("Updating member with ID: {}", id);
        
        // Validate member
        validateMember(memberDetails);
        
        // Check if member exists
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
        
        // Check if email is taken by another member
        Optional<Member> memberWithEmail = memberRepository.findByEmail(memberDetails.getEmail());
        if (memberWithEmail.isPresent() && !memberWithEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Email already in use by another member: " + memberDetails.getEmail());
        }
        
        // Update member details
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        
        // Save and publish event
        Member updatedMember = memberRepository.save(existingMember);
        eventPublisher.publishEvent(updatedMember);
        
        log.info("Successfully updated member with ID: {}", id);
        return updatedMember;
    }

    /**
     * Deletes a member by their ID.
     *
     * @param id the ID of the member to delete
     * @throws IllegalArgumentException if the member doesn't exist
     */
    public void deleteMember(Long id) {
        log.info("Deleting member with ID: {}", id);
        
        // Check if member exists
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with ID: " + id);
        }
        
        memberRepository.deleteById(id);
        log.info("Successfully deleted member with ID: {}", id);
    }

    /**
     * Validates a member using Bean Validation.
     *
     * @param member the member to validate
     * @throws ConstraintViolationException if validation fails
     */
    private void validateMember(Member member) {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        if (!violations.isEmpty()) {
            log.error("Member validation failed with {} violations", violations.size());
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
    }
}
