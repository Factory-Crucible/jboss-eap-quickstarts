package com.example.kitchensink.service;

import java.util.List;

import com.example.kitchensink.model.Member;

/**
 * Service interface for managing {@link Member} entities.
 * This interface defines all business operations for working with members.
 */
public interface MemberService {
    
    /**
     * Find a member by their ID.
     * 
     * @param id the ID of the member to find
     * @return the found member
     * @throws com.example.kitchensink.exception.ResourceNotFoundException if no member is found with the given ID
     */
    Member findById(Long id);
    
    /**
     * Find all members ordered by name.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllMembers();
    
    /**
     * Find a member by their email address.
     * 
     * @param email the email address to search for
     * @return the found member or null if not found
     */
    Member findByEmail(String email);
    
    /**
     * Check if a member with the given email already exists.
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean emailExists(String email);
    
    /**
     * Register a new member.
     * 
     * @param member the member to register
     * @return the registered member with ID assigned
     * @throws com.example.kitchensink.exception.DuplicateResourceException if a member with the same email already exists
     */
    Member register(Member member);
    
    /**
     * Update an existing member.
     * 
     * @param id the ID of the member to update
     * @param member the updated member data
     * @return the updated member
     * @throws com.example.kitchensink.exception.ResourceNotFoundException if no member is found with the given ID
     * @throws com.example.kitchensink.exception.DuplicateResourceException if the updated email conflicts with an existing member
     */
    Member updateMember(Long id, Member member);
    
    /**
     * Delete a member by their ID.
     * 
     * @param id the ID of the member to delete
     * @throws com.example.kitchensink.exception.ResourceNotFoundException if no member is found with the given ID
     */
    void deleteMember(Long id);
}
