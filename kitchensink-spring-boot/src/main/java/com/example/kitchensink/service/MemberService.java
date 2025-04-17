package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Member entities.
 * Provides methods for registration, finding, and listing members.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;

    /**
     * Registers a new member.
     * Validates the member and checks for duplicate email addresses.
     *
     * @param member the member to register
     * @return the registered member with ID
     * @throws ValidationException if the email is already in use
     */
    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            log.error("Email {} already exists", member.getEmail());
            throw new ValidationException("Email already exists");
        }
        
        return memberRepository.save(member);
    }
    
    /**
     * Checks if a member with the given email already exists.
     *
     * @param email the email to check
     * @return true if the email is already in use, false otherwise
     */
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Finds a member by ID.
     *
     * @param id the ID of the member to find
     * @return an Optional containing the member, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        log.debug("Finding member with ID: {}", id);
        return memberRepository.findById(id);
    }
    
    /**
     * Finds a member by email.
     *
     * @param email the email of the member to find
     * @return an Optional containing the member, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        log.debug("Finding member with email: {}", email);
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Lists all members ordered by name.
     *
     * @return a list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        log.debug("Finding all members ordered by name");
        return memberRepository.findAllByOrderByNameAsc();
    }
}
