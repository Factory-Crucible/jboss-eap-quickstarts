package com.example.kitchensink.service;

import java.util.List;
import java.util.Optional;

import com.example.kitchensink.model.Member;

/**
 * Service interface for Member-related operations.
 * 
 * This interface defines the business operations available for Member entities,
 * replacing the functionality of the original JBoss MemberRegistration class
 * while following Spring Boot best practices.
 */
public interface MemberService {
    
    /**
     * Register a new member.
     * 
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws Exception if registration fails (e.g., email already exists)
     */
    Member register(Member member) throws Exception;
    
    /**
     * Find a member by their ID.
     * 
     * @param id The ID of the member to find
     * @return An Optional containing the member if found
     */
    Optional<Member> findById(Long id);
    
    /**
     * Find a member by their email address.
     * 
     * @param email The email address to search for
     * @return An Optional containing the member if found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Get all members ordered by name.
     * 
     * @return A list of all members, sorted alphabetically by name
     */
    List<Member> findAllOrderedByName();
    
    /**
     * Update an existing member.
     * 
     * @param id The ID of the member to update
     * @param memberDetails The updated member details
     * @return The updated member
     * @throws Exception if update fails (e.g., member not found)
     */
    Member update(Long id, Member memberDetails) throws Exception;
    
    /**
     * Delete a member by their ID.
     * 
     * @param id The ID of the member to delete
     * @throws Exception if deletion fails (e.g., member not found)
     */
    void delete(Long id) throws Exception;
    
    /**
     * Check if a member with the given email already exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean emailExists(String email);
}
