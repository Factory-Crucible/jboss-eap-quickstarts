package com.example.kitchensink.service.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link MemberService} interface.
 * This class handles the business logic for member operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Member findById(Long id) {
        log.debug("Finding member with id: {}", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Override
    public List<Member> findAllMembers() {
        log.debug("Finding all members ordered by name");
        return memberRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Member findByEmail(String email) {
        log.debug("Finding member with email: {}", email);
        return memberRepository.findByEmail(email)
                .orElse(null);
    }

    @Override
    public boolean emailExists(String email) {
        log.debug("Checking if email exists: {}", email);
        return memberRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public Member register(Member member) {
        log.info("Registering new member: {}", member.getName());
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            log.warn("Email already exists: {}", member.getEmail());
            throw new DuplicateResourceException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish an event that a new member has been registered
        log.debug("Publishing member registered event for: {}", savedMember.getName());
        eventPublisher.publishEvent(savedMember);
        
        log.info("Successfully registered member with id: {}", savedMember.getId());
        return savedMember;
    }

    @Override
    @Transactional
    public Member updateMember(Long id, Member member) {
        log.info("Updating member with id: {}", id);
        
        // Check if member exists
        Member existingMember = findById(id);
        
        // Check if email is being changed and if it conflicts with another member
        if (!existingMember.getEmail().equals(member.getEmail()) && emailExists(member.getEmail())) {
            log.warn("Cannot update member. Email already exists: {}", member.getEmail());
            throw new DuplicateResourceException("Email already exists: " + member.getEmail());
        }
        
        // Update the member fields
        existingMember.setName(member.getName());
        existingMember.setEmail(member.getEmail());
        existingMember.setPhoneNumber(member.getPhoneNumber());
        
        // Save the updated member
        Member updatedMember = memberRepository.save(existingMember);
        
        // Publish an event that a member has been updated
        log.debug("Publishing member updated event for: {}", updatedMember.getName());
        eventPublisher.publishEvent(updatedMember);
        
        log.info("Successfully updated member with id: {}", updatedMember.getId());
        return updatedMember;
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.info("Deleting member with id: {}", id);
        
        // Check if member exists
        Member existingMember = findById(id);
        
        // Delete the member
        memberRepository.delete(existingMember);
        
        // Publish an event that a member has been deleted
        log.debug("Publishing member deleted event for: {}", existingMember.getName());
        eventPublisher.publishEvent(existingMember);
        
        log.info("Successfully deleted member with id: {}", id);
    }
}
