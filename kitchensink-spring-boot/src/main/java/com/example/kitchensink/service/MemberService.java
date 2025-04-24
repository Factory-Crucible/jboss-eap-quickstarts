package com.example.kitchensink.service;

import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Member entities.
 * 
 * This interface defines the business operations available for Member entities,
 * including registration, retrieval, and search functionality.
 */
public interface MemberService {
    
    /**
     * Registers a new member in the system.
     * 
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws DuplicateResourceException If a member with the same email already exists
     */
    Member register(Member member) throws DuplicateResourceException;
    
    /**
     * Retrieves all members ordered by name.
     * 
     * @return A list of all members ordered by name
     */
    List<Member> findAllOrderedByName();
    
    /**
     * Finds a member by their ID.
     * 
     * @param id The ID of the member to find
     * @return The found member
     * @throws ResourceNotFoundException If no member with the given ID exists
     */
    Member findById(Long id) throws ResourceNotFoundException;
    
    /**
     * Finds a member by their email address.
     * 
     * @param email The email address to search for
     * @return The found member
     * @throws ResourceNotFoundException If no member with the given email exists
     */
    Member findByEmail(String email) throws ResourceNotFoundException;
    
    /**
     * Checks if a member with the given email already exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean emailExists(String email);
}
