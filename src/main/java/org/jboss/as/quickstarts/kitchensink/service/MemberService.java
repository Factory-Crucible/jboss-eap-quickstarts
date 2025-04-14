package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for Member entity operations
 * Provides business logic and transaction management for Member entities
 */
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final Validator validator;

    /**
     * Constructor injection for dependencies
     */
    public MemberService(MemberRepository memberRepository, Validator validator) {
        this.memberRepository = memberRepository;
        this.validator = validator;
    }

    /**
     * Register a new member
     *
     * @param member the member to register
     * @return the registered member with ID
     * @throws ConstraintViolationException if validation fails
     * @throws ServiceException if member with email already exists
     */
    @Transactional
    public Member register(Member member) {
        log.info("Registering {}", member.getName());
        
        // Validate member using Bean Validation
        validateMember(member);
        
        // Check if member with email already exists
        if (emailExists(member.getEmail())) {
            throw new ServiceException("Email already exists: " + member.getEmail());
        }
        
        return memberRepository.save(member);
    }

    /**
     * Find all members ordered by name
     *
     * @return list of all members
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Find member by ID
     *
     * @param id the member ID
     * @return the member if found
     * @throws ServiceException if member not found
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Member not found with ID: " + id));
    }

    /**
     * Update an existing member
     *
     * @param id the member ID
     * @param updatedMember the updated member data
     * @return the updated member
     * @throws ServiceException if member not found or email already exists
     */
    @Transactional
    public Member update(Long id, Member updatedMember) {
        log.info("Updating member with ID: {}", id);
        
        // Validate member using Bean Validation
        validateMember(updatedMember);
        
        Member existingMember = findById(id);
        
        // Check if email is being changed and if it already exists
        if (!existingMember.getEmail().equals(updatedMember.getEmail()) 
                && emailExists(updatedMember.getEmail())) {
            throw new ServiceException("Email already exists: " + updatedMember.getEmail());
        }
        
        // Update fields
        existingMember.setName(updatedMember.getName());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
        
        return memberRepository.save(existingMember);
    }

    /**
     * Delete a member by ID
     *
     * @param id the member ID
     * @throws ServiceException if member not found
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting member with ID: {}", id);
        
        if (!memberRepository.existsById(id)) {
            throw new ServiceException("Member not found with ID: " + id);
        }
        
        memberRepository.deleteById(id);
    }

    /**
     * Check if a member with the given email exists
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    private boolean emailExists(String email) {
        return memberRepository.findByEmail(email) != null;
    }

    /**
     * Validate member using Bean Validation
     *
     * @param member the member to validate
     * @throws ConstraintViolationException if validation fails
     */
    private void validateMember(Member member) {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
    }

    /**
     * Custom service exception
     */
    public static class ServiceException extends RuntimeException {
        public ServiceException(String message) {
            super(message);
        }
    }
}
