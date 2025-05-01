package com.example.kitchensink.service;

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link MemberService} interface.
 * This class provides the business logic for member management operations
 * and interacts with the {@link MemberRepository} to perform data access.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Member register(Member member) {
        log.info("Registering new member with email: {}", member.getEmail());
        
        if (memberRepository.existsByEmail(member.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", member.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        Member savedMember = memberRepository.save(member);
        log.info("Member registered successfully with ID: {}", savedMember.getId());
        return savedMember;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        log.debug("Retrieving all members");
        return memberRepository.findAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Member> findAllMembers(Pageable pageable) {
        log.debug("Retrieving paged members with pageable: {}", pageable);
        return memberRepository.findAll(pageable);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        log.debug("Finding member by ID: {}", id);
        return memberRepository.findById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        log.debug("Finding member by email: {}", email);
        return memberRepository.findByEmail(email);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Member> findByNameContaining(String name) {
        log.debug("Finding members with name containing: {}", name);
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailUnique(String email) {
        log.debug("Checking if email is unique: {}", email);
        return !memberRepository.existsByEmail(email);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Member update(Member member) {
        log.info("Updating member with ID: {}", member.getId());
        
        // Verify the member exists
        Member existingMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", member.getId()));
        
        // Check if email is changed and if new email already exists for another member
        if (!existingMember.getEmail().equals(member.getEmail()) && 
                memberRepository.existsByEmail(member.getEmail())) {
            log.warn("Update failed: New email already exists: {}", member.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        Member updatedMember = memberRepository.save(member);
        log.info("Member updated successfully with ID: {}", updatedMember.getId());
        return updatedMember;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        log.info("Deleting member with ID: {}", id);
        
        // Verify the member exists
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member", "id", id);
        }
        
        memberRepository.deleteById(id);
        log.info("Member deleted successfully with ID: {}", id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Member> searchMembers(String searchTerm, Pageable pageable) {
        log.debug("Searching members with term: {} and pageable: {}", searchTerm, pageable);
        return memberRepository.searchMembers(searchTerm, pageable);
    }
}
