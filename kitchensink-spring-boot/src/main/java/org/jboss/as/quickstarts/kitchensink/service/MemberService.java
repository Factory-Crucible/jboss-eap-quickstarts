package org.jboss.as.quickstarts.kitchensink.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

/**
 * Service handling business logic for Member entities.
 * Provides methods for registration, retrieval, and management of members.
 */
@Service
@Validated
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
     * Registers a new member in the system.
     * 
     * @param member the member to register
     * @return the registered member with ID assigned
     * @throws ValidationException if the member's email is already in use
     */
    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering " + member.getName());
        
        // Check if email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new ValidationException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish an event to notify other components
        eventPublisher.publishEvent(savedMember);
        
        return savedMember;
    }
    
    /**
     * Retrieves all members ordered by name.
     * 
     * @return list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
    
    /**
     * Finds a member by email.
     * 
     * @param email the email address
     * @return optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Updates an existing member.
     * 
     * @param id the ID of the member to update
     * @param updatedMember the updated member data
     * @return the updated member
     * @throws IllegalArgumentException if the member is not found
     * @throws ValidationException if the email is already in use by another member
     */
    @Transactional
    public Member updateMember(Long id, @Valid Member updatedMember) {
        return memberRepository.findById(id)
            .map(existingMember -> {
                // Check if email is being changed and already exists
                if (!existingMember.getEmail().equals(updatedMember.getEmail()) 
                        && memberRepository.existsByEmail(updatedMember.getEmail())) {
                    throw new ValidationException("Email already exists: " + updatedMember.getEmail());
                }
                
                existingMember.setName(updatedMember.getName());
                existingMember.setEmail(updatedMember.getEmail());
                existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
                
                Member saved = memberRepository.save(existingMember);
                eventPublisher.publishEvent(saved);
                return saved;
            })
            .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
    }
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the ID of the member to delete
     * @throws IllegalArgumentException if the member is not found
     */
    @Transactional
    public void deleteMember(Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
    }
}
