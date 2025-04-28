package com.example.kitchensink.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kitchensink.event.MemberRegistrationEvent;
import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.MemberNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;

/**
 * Service for Member operations.
 * Provides methods to register, find, and update members.
 */
@Service
public class MemberService {

    private final Logger log = Logger.getLogger(MemberService.class.getName());
    
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Register a new member
     * 
     * @param member the member to register
     * @return the registered member with ID
     * @throws EmailAlreadyExistsException if a member with the same email already exists
     */
    @Transactional
    public Member register(Member member) {
        log.info("Registering " + member.getName());
        
        // Check if email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish event
        eventPublisher.publishEvent(new MemberRegistrationEvent(this, savedMember));
        
        return savedMember;
    }
    
    /**
     * Find a member by ID
     * 
     * @param id the member ID
     * @return the member
     * @throws MemberNotFoundException if no member is found with the given ID
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("No member found with ID: " + id));
    }
    
    /**
     * Find a member by email
     * 
     * @param email the email to search for
     * @return the member
     * @throws MemberNotFoundException if no member is found with the given email
     */
    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("No member found with email: " + email));
    }
    
    /**
     * Check if a member with the given email exists
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberRepository.existsByEmail(email);
    }
    
    /**
     * Get all members ordered by name
     * 
     * @return list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Update an existing member
     * 
     * @param id the ID of the member to update
     * @param updatedMember the updated member data
     * @return the updated member
     * @throws MemberNotFoundException if no member is found with the given ID
     * @throws EmailAlreadyExistsException if the new email already exists for another member
     */
    @Transactional
    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = findById(id);
        
        // Check if email is being changed and if it already exists
        if (!existingMember.getEmail().equals(updatedMember.getEmail()) && 
            memberRepository.existsByEmail(updatedMember.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + updatedMember.getEmail());
        }
        
        // Update the member
        existingMember.setName(updatedMember.getName());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
        
        Member saved = memberRepository.save(existingMember);
        
        // Publish event
        eventPublisher.publishEvent(new MemberRegistrationEvent(this, saved));
        
        return saved;
    }
    
    /**
     * Delete a member by ID
     * 
     * @param id the ID of the member to delete
     * @throws MemberNotFoundException if no member is found with the given ID
     */
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new MemberNotFoundException("No member found with ID: " + id);
        }
        memberRepository.deleteById(id);
    }
}
