package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link Member} entities.
 * This interface defines the business operations available for member management,
 * including CRUD operations and additional business logic.
 */
public interface MemberService {
    
    /**
     * Registers a new member in the system.
     * This method validates the member data and ensures the email is unique
     * before saving the member to the database.
     * 
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws com.example.kitchensink.exception.EmailAlreadyExistsException if the email already exists
     */
    Member register(Member member);
    
    /**
     * Retrieves all members from the system.
     * 
     * @return a list of all members
     */
    List<Member> findAllMembers();
    
    /**
     * Retrieves a paginated list of members.
     * 
     * @param pageable pagination information
     * @return a page of members
     */
    Page<Member> findAllMembers(Pageable pageable);
    
    /**
     * Finds a member by their ID.
     * 
     * @param id the ID of the member to find
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findById(Long id);
    
    /**
     * Finds a member by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Finds members whose names contain the given substring (case-insensitive).
     * 
     * @param name the substring to search for in member names
     * @return a list of members whose names contain the given substring
     */
    List<Member> findByNameContaining(String name);
    
    /**
     * Checks if an email address is unique (not already used by another member).
     * 
     * @param email the email address to check
     * @return true if the email is unique, false otherwise
     */
    boolean isEmailUnique(String email);
    
    /**
     * Updates an existing member's information.
     * This method validates the member data and ensures the email is unique
     * (or unchanged for the same member) before updating the member in the database.
     * 
     * @param member the member with updated information
     * @return the updated member
     * @throws com.example.kitchensink.exception.ResourceNotFoundException if the member doesn't exist
     * @throws com.example.kitchensink.exception.EmailAlreadyExistsException if the email already exists for another member
     */
    Member update(Member member);
    
    /**
     * Deletes a member by their ID.
     * 
     * @param id the ID of the member to delete
     * @throws com.example.kitchensink.exception.ResourceNotFoundException if the member doesn't exist
     */
    void delete(Long id);
    
    /**
     * Searches for members by a search term that matches either name or email.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return a page of members matching the search criteria
     */
    Page<Member> searchMembers(String searchTerm, Pageable pageable);
}
