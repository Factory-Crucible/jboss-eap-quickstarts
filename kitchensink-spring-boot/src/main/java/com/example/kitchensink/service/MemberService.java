package com.example.kitchensink.service;

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
 * Service for Member registration and management.
 * This class replaces the MemberRegistration EJB from the original JBoss application.
 * It handles business logic related to member operations and uses Spring's transaction management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Registers a new member.
     * Checks if the email already exists before persisting the member.
     * 
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws IllegalArgumentException if a member with the same email already exists
     */
    @Transactional
    public Member register(Member member) {
        log.info("Registering member: {}", member.getName());
        
        // Check if email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            log.error("Email already exists: {}", member.getEmail());
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        
        Member savedMember = memberRepository.save(member);
        log.info("Successfully registered member with ID: {}", savedMember.getId());
        
        // Publish event that a new member has been registered
        eventPublisher.publishEvent(savedMember);
        
        return savedMember;
    }
    
    /**
     * Finds a member by ID.
     * 
     * @param id the ID of the member to find
     * @return the found member
     * @throws IllegalArgumentException if no member is found with the given ID
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
    }
    
    /**
     * Finds a member by email.
     * 
     * @param email the email of the member to find
     * @return an Optional containing the member if found, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Retrieves all members ordered by name.
     * 
     * @return a list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }
    
    /**
     * Updates an existing member.
     * 
     * @param member the member to update
     * @return the updated member
     * @throws IllegalArgumentException if the member does not exist
     */
    @Transactional
    public Member update(Member member) {
        if (member.getId() == null || !memberRepository.existsById(member.getId())) {
            throw new IllegalArgumentException("Cannot update non-existent member");
        }
        
        // Check if email is being changed and if the new email already exists
        Optional<Member> existingWithEmail = memberRepository.findByEmail(member.getEmail());
        if (existingWithEmail.isPresent() && !existingWithEmail.get().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        
        Member updatedMember = memberRepository.save(member);
        eventPublisher.publishEvent(updatedMember);
        
        return updatedMember;
    }
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the ID of the member to delete
     * @throws IllegalArgumentException if no member is found with the given ID
     */
    @Transactional
    public void delete(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with ID: " + id);
        }
        
        memberRepository.deleteById(id);
    }
}
