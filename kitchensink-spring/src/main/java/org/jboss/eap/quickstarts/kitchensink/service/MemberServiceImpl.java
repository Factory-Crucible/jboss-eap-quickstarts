package org.jboss.eap.quickstarts.kitchensink.service;

import org.jboss.eap.quickstarts.kitchensink.model.Member;
import org.jboss.eap.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the MemberService interface.
 * This class provides the business logic for Member operations.
 * 
 * Migrated from the JBoss EAP Kitchensink application's MemberRegistration class.
 * The original @Stateless EJB has been replaced with Spring's @Service and @Transactional.
 * Event handling is implemented using Spring's ApplicationEventPublisher.
 */
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new member.
     * This method validates that the email is unique before persisting the member.
     * After successful registration, it fires a member event.
     * 
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws Exception if registration fails (e.g., duplicate email)
     */
    @Override
    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            throw new ValidationException("Email already exists: " + member.getEmail());
        }
        
        // Persist the member
        Member savedMember = memberRepository.save(member);
        
        // Fire event (equivalent to the CDI event in the original application)
        eventPublisher.publishEvent(savedMember);
        
        return savedMember;
    }

    /**
     * Find a member by ID.
     * 
     * @param id The ID of the member to find
     * @return Optional containing the member if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Find a member by email.
     * 
     * @param email The email of the member to find
     * @return Optional containing the member if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Get all members ordered by name.
     * 
     * @return List of all members ordered by name
     */
    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }

    /**
     * Update an existing member.
     * 
     * @param id The ID of the member to update
     * @param memberDetails The updated member details
     * @return The updated member
     * @throws Exception if update fails (e.g., member not found)
     */
    @Override
    @Transactional
    public Member updateMember(Long id, Member memberDetails) throws Exception {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
        
        // Check if email is being changed and if the new email already exists
        if (!existingMember.getEmail().equals(memberDetails.getEmail()) 
                && emailExists(memberDetails.getEmail())) {
            throw new ValidationException("Email already exists: " + memberDetails.getEmail());
        }
        
        // Update the member fields
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        
        // Save the updated member
        Member updatedMember = memberRepository.save(existingMember);
        
        // Fire event
        eventPublisher.publishEvent(updatedMember);
        
        return updatedMember;
    }

    /**
     * Delete a member by ID.
     * 
     * @param id The ID of the member to delete
     * @throws Exception if deletion fails (e.g., member not found)
     */
    @Override
    @Transactional
    public void deleteMember(Long id) throws Exception {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
        
        memberRepository.delete(member);
        
        // Fire event with the deleted member
        eventPublisher.publishEvent(member);
    }

    /**
     * Check if a member with the given email exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberRepository.existsByEmail(email);
    }
}
