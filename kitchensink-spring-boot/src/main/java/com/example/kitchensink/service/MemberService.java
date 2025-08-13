package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for managing Member entities.
 * This class replaces the EJB MemberRegistration from the original JBoss application.
 * It uses Spring's @Service and @Transactional annotations for business logic and transaction management.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    private final Validator validator;

    /**
     * Registers a new member after validating the member data.
     * This method replaces the register method from the original MemberRegistration EJB.
     *
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws ConstraintViolationException if bean validation fails
     * @throws ValidationException if a member with the same email already exists
     */
    @Transactional
    public Member registerMember(Member member) {
        log.info("Registering member: {}", member.getName());
        
        // Validate member using bean validation
        validateMember(member);
        
        // Save the member to the database
        return memberRepository.save(member);
    }

    /**
     * Finds all members ordered by name.
     *
     * @return a list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        log.debug("Finding all members ordered by name");
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Finds a member by ID.
     *
     * @param id the ID of the member to find
     * @return the member with the specified ID
     * @throws EntityNotFoundException if no member is found with the given ID
     */
    @Transactional(readOnly = true)
    public Member findMemberById(Long id) {
        log.debug("Finding member by ID: {}", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + id));
    }

    /**
     * Finds a member by email.
     *
     * @param email the email of the member to find
     * @return the member with the specified email, or null if none found
     */
    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        log.debug("Finding member by email: {}", email);
        return memberRepository.findByEmail(email);
    }

    /**
     * Checks if a member with the given email already exists.
     *
     * @param email the email to check
     * @return true if the email is already taken, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isEmailAlreadyTaken(String email) {
        log.debug("Checking if email is already taken: {}", email);
        return memberRepository.findByEmail(email) != null;
    }

    /**
     * Validates the given Member using Bean Validation and checks for email uniqueness.
     *
     * @param member the member to validate
     * @throws ConstraintViolationException if bean validation fails
     * @throws ValidationException if a member with the same email already exists
     */
    private void validateMember(Member member) {
        // Perform bean validation
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Member validation failed", new HashSet<>(violations));
        }

        // Check email uniqueness
        if (isEmailAlreadyTaken(member.getEmail())) {
            throw new ValidationException("Email already exists: " + member.getEmail());
        }
    }
}
