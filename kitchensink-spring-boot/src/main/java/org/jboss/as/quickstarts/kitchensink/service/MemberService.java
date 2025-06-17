package org.jboss.as.quickstarts.kitchensink.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.as.quickstarts.kitchensink.exception.UniqueEmailException;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Service for managing Member entities.
 * This class replaces the JBoss EAP MemberRegistration EJB with Spring Boot service patterns.
 * It handles member registration, lookup, and validation.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Register a new member after validation.
     * 
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws UniqueEmailException if a member with the same email already exists
     */
    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            throw new UniqueEmailException("Unique Email Violation");
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish an event similar to the CDI event in the original code
        eventPublisher.publishEvent(savedMember);
        
        return savedMember;
    }

    /**
     * Find a member by ID.
     * 
     * @param id the member ID
     * @return the member
     * @throws EntityNotFoundException if no member is found with the given ID
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + id));
    }

    /**
     * Find a member by email.
     * 
     * @param email the email address
     * @return the member
     * @throws EntityNotFoundException if no member is found with the given email
     */
    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + email));
    }

    /**
     * Get all members ordered by name.
     * 
     * @return list of all members
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Check if a member with the given email already exists.
     * 
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * Update an existing member.
     * 
     * @param id the ID of the member to update
     * @param memberDetails the updated member details
     * @return the updated member
     * @throws EntityNotFoundException if no member is found with the given ID
     * @throws UniqueEmailException if the new email conflicts with an existing member
     */
    @Transactional
    public Member updateMember(Long id, Member memberDetails) {
        Member existingMember = findById(id);
        
        // If email is changing, check if the new email already exists
        if (!existingMember.getEmail().equals(memberDetails.getEmail()) && 
            emailExists(memberDetails.getEmail())) {
            throw new UniqueEmailException("Email already in use");
        }
        
        // Update fields
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        
        log.info("Updating member: {}", existingMember.getName());
        return memberRepository.save(existingMember);
    }

    /**
     * Delete a member by ID.
     * 
     * @param id the ID of the member to delete
     * @throws EntityNotFoundException if no member is found with the given ID
     */
    @Transactional
    public void deleteMember(Long id) {
        Member member = findById(id);
        log.info("Deleting member: {}", member.getName());
        memberRepository.delete(member);
    }
}
