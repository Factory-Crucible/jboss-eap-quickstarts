package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for {@link Member} entities.
 * This interface defines methods for CRUD operations and other business logic related to members.
 */
public interface MemberService {
    
    /**
     * Find all members.
     *
     * @return a list of all members
     */
    List<Member> findAll();
    
    /**
     * Find a member by ID.
     *
     * @param id the member ID
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findById(Long id);
    
    /**
     * Find a member by email.
     *
     * @param email the email address
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Register a new member.
     *
     * @param member the member to register
     * @return the registered member with ID assigned
     */
    Member register(Member member);
    
    /**
     * Update an existing member.
     *
     * @param id the ID of the member to update
     * @param memberDetails the updated member details
     * @return the updated member
     * @throws com.example.kitchensink.exception.ResourceNotFoundException if the member is not found
     */
    Member update(Long id, Member memberDetails);
    
    /**
     * Delete a member by ID.
     *
     * @param id the ID of the member to delete
     * @return true if the member was deleted, false otherwise
     */
    boolean delete(Long id);
}
