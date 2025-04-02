package com.example.kitchensink.service;

import java.util.List;
import java.util.Optional;

import com.example.kitchensink.model.Member;

/**
 * Service interface for {@link Member} operations.
 * This interface defines the contract for member management functionality.
 */
public interface MemberService {
    
    /**
     * Register a new member.
     * 
     * @param member the member to register
     * @return the registered member with ID populated
     * @throws Exception if registration fails, e.g., due to duplicate email
     */
    Member register(Member member) throws Exception;
    
    /**
     * Find a member by ID.
     * 
     * @param id the ID of the member to find
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findById(Long id);
    
    /**
     * Find a member by email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Get all members ordered by name.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> getAllMembersOrderedByName();
    
    /**
     * Check if a member with the given email already exists.
     * 
     * @param email the email address to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean emailExists(String email);
}
