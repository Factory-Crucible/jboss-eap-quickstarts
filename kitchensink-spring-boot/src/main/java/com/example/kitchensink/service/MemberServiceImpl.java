package com.example.kitchensink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kitchensink.event.MemberRegistrationEvent;
import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.MemberNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the MemberService interface.
 * 
 * This service handles business logic related to Member entities, replacing
 * the original JBoss MemberRegistration EJB with a Spring Boot service.
 * It uses Spring's declarative transaction management instead of EJB's
 * container-managed transactions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Register a new member.
     * 
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws Exception if registration fails (e.g., email already exists)
     */
    @Override
    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        if (emailExists(member.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + member.getEmail() + " already exists");
        }
        
        Member savedMember = memberRepository.save(member);
        
        // Publish an event that a new member has been registered
        eventPublisher.publishEvent(new MemberRegistrationEvent(savedMember));
        
        return savedMember;
    }

    /**
     * Find a member by their ID.
     * 
     * @param id The ID of the member to find
     * @return An Optional containing the member if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Find a member by their email address.
     * 
     * @param email The email address to search for
     * @return An Optional containing the member if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Get all members ordered by name.
     * 
     * @return A list of all members, sorted alphabetically by name
     */
    @Override
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
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
    public Member update(Long id, Member memberDetails) throws Exception {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
        
        // Check if email is being changed and if it already exists
        if (!existingMember.getEmail().equals(memberDetails.getEmail()) 
                && emailExists(memberDetails.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + memberDetails.getEmail() + " already exists");
        }
        
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        
        log.info("Updating member: {}", existingMember.getName());
        return memberRepository.save(existingMember);
    }

    /**
     * Delete a member by their ID.
     * 
     * @param id The ID of the member to delete
     * @throws Exception if deletion fails (e.g., member not found)
     */
    @Override
    @Transactional
    public void delete(Long id) throws Exception {
        if (!memberRepository.existsById(id)) {
            throw new MemberNotFoundException("Member not found with id: " + id);
        }
        
        log.info("Deleting member with id: {}", id);
        memberRepository.deleteById(id);
    }

    /**
     * Check if a member with the given email already exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
