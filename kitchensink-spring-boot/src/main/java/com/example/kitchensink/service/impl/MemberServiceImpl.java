package com.example.kitchensink.service.impl;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link MemberService} interface.
 * This class handles business logic for member operations.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        log.debug("Finding all members");
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findAllMembersOrderedByName() {
        log.debug("Finding all members ordered by name");
        return memberRepository.findAllOrderedByName();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(Long id) {
        log.debug("Finding member with ID: {}", id);
        return memberRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findMemberByEmail(String email) {
        log.debug("Finding member with email: {}", email);
        return memberRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Member createMember(@Valid Member member) {
        log.debug("Creating new member: {}", member);
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            log.error("Email already exists: {}", member.getEmail());
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        
        try {
            Member savedMember = memberRepository.save(member);
            log.info("Member created successfully with ID: {}", savedMember.getId());
            return savedMember;
        } catch (Exception e) {
            log.error("Error creating member", e);
            throw new ValidationException("Error creating member: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Member updateMember(Long id, @Valid Member member) {
        log.debug("Updating member with ID: {}", id);
        
        // Check if member exists
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Member not found with ID: {}", id);
                    return new IllegalArgumentException("Member not found with ID: " + id);
                });
        
        // Check if email is already in use by another member
        Optional<Member> memberWithEmail = memberRepository.findByEmail(member.getEmail());
        if (memberWithEmail.isPresent() && !memberWithEmail.get().getId().equals(id)) {
            log.error("Email already in use by another member: {}", member.getEmail());
            throw new IllegalArgumentException("Email already in use by another member: " + member.getEmail());
        }
        
        // Update member fields
        existingMember.setName(member.getName());
        existingMember.setEmail(member.getEmail());
        existingMember.setPhoneNumber(member.getPhoneNumber());
        
        try {
            Member updatedMember = memberRepository.save(existingMember);
            log.info("Member updated successfully: {}", updatedMember);
            return updatedMember;
        } catch (Exception e) {
            log.error("Error updating member", e);
            throw new ValidationException("Error updating member: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.debug("Deleting member with ID: {}", id);
        
        // Check if member exists
        if (!memberRepository.existsById(id)) {
            log.error("Member not found with ID: {}", id);
            throw new IllegalArgumentException("Member not found with ID: " + id);
        }
        
        try {
            memberRepository.deleteById(id);
            log.info("Member deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting member", e);
            throw new RuntimeException("Error deleting member: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        log.debug("Checking if email exists: {}", email);
        return memberRepository.existsByEmail(email);
    }
}
