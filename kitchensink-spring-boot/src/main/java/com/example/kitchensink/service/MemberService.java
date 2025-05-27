package com.example.kitchensink.service;

import com.example.kitchensink.exception.DuplicateEmailException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for member management operations.
 * This class handles business logic related to members, including
 * registration, validation, and retrieval operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final Validator validator;

    /**
     * Registers a new member after validation.
     *
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws ConstraintViolationException if bean validation fails
     * @throws DuplicateEmailException if the email is already in use
     */
    @Transactional
    public Member register(Member member) {
        log.debug("Registering member: {}", member.getEmail());
        
        // Validate the member using Bean Validation
        validateMember(member);
        
        // Check if email already exists
        if (emailAlreadyExists(member.getEmail())) {
            log.error("Email already exists: {}", member.getEmail());
            throw new DuplicateEmailException("Email already in use: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        log.info("Successfully registered member: {} with ID: {}", savedMember.getEmail(), savedMember.getId());
        
        return savedMember;
    }

    /**
     * Retrieves all members ordered by name.
     *
     * @return a list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        log.debug("Finding all members ordered by name");
        return memberRepository.findAllOrderedByName();
    }

    /**
     * Finds a member by ID.
     *
     * @param id the member ID
     * @return the member
     * @throws EntityNotFoundException if the member is not found
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        log.debug("Finding member by ID: {}", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + id));
    }

    /**
     * Finds a member by email.
     *
     * @param email the email address
     * @return an Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        log.debug("Finding member by email: {}", email);
        return memberRepository.findByEmail(email);
    }

    /**
     * Checks if a member with the given email already exists.
     *
     * @param email the email to check
     * @return true if the email is already in use, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailAlreadyExists(String email) {
        log.debug("Checking if email already exists: {}", email);
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * Validates a member using Bean Validation.
     *
     * @param member the member to validate
     * @throws ConstraintViolationException if validation fails
     */
    private void validateMember(Member member) {
        log.debug("Validating member: {}", member.getEmail());
        
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        if (!violations.isEmpty()) {
            log.error("Member validation failed with {} violations", violations.size());
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
        
        log.debug("Member validation successful");
    }
}
