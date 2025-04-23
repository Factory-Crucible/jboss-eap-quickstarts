package com.example.kitchensink.service;

import com.example.kitchensink.event.MemberRegisteredEvent;
import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Member entities.
 * Provides business logic and transaction management.
 * Migrated from JBoss EAP MemberRegistration EJB to Spring Boot Service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Retrieves all members ordered by name.
     *
     * @return list of all members
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        log.info("Retrieving all members ordered by name");
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Retrieves a member by ID.
     *
     * @param id the member ID
     * @return the member
     * @throws ResourceNotFoundException if the member is not found
     */
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        log.info("Retrieving member with ID: {}", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    /**
     * Checks if a member with the given email exists.
     *
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        log.debug("Checking if email exists: {}", email);
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * Registers a new member.
     * Validates that the email is unique before saving.
     *
     * @param member the member to register
     * @return the registered member with ID
     * @throws EmailAlreadyExistsException if the email already exists
     */
    @Transactional
    public Member register(Member member) {
        log.info("Registering new member: {}", member.getName());
        
        // Check if email already exists
        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
        if (existingMember.isPresent()) {
            log.error("Email already exists: {}", member.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        log.info("Successfully registered member with ID: {}", savedMember.getId());
        
        // Publish event (equivalent to JBoss CDI event)
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        return savedMember;
    }

    /**
     * Updates an existing member.
     * Validates that the email is unique before updating.
     *
     * @param id the ID of the member to update
     * @param memberDetails the updated member details
     * @return the updated member
     * @throws ResourceNotFoundException if the member is not found
     * @throws EmailAlreadyExistsException if the new email already exists for another member
     */
    @Transactional
    public Member updateMember(Long id, Member memberDetails) {
        log.info("Updating member with ID: {}", id);
        
        // Find the existing member
        Member existingMember = getMemberById(id);
        
        // Check if email is being changed and if it already exists
        if (!existingMember.getEmail().equals(memberDetails.getEmail())) {
            Optional<Member> memberWithEmail = memberRepository.findByEmail(memberDetails.getEmail());
            if (memberWithEmail.isPresent() && !memberWithEmail.get().getId().equals(id)) {
                log.error("Cannot update member. Email already exists: {}", memberDetails.getEmail());
                throw new EmailAlreadyExistsException("Email already exists: " + memberDetails.getEmail());
            }
        }
        
        // Update member details
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        
        // Save and return the updated member
        Member updatedMember = memberRepository.save(existingMember);
        log.info("Successfully updated member with ID: {}", updatedMember.getId());
        
        return updatedMember;
    }

    /**
     * Deletes a member by ID.
     *
     * @param id the ID of the member to delete
     * @throws ResourceNotFoundException if the member is not found
     */
    @Transactional
    public void deleteMember(Long id) {
        log.info("Deleting member with ID: {}", id);
        
        // Verify member exists
        Member member = getMemberById(id);
        
        // Delete the member
        memberRepository.delete(member);
        log.info("Successfully deleted member with ID: {}", id);
    }
}
