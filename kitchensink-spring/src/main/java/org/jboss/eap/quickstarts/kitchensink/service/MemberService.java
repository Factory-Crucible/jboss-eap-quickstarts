package org.jboss.eap.quickstarts.kitchensink.service;

import org.jboss.eap.quickstarts.kitchensink.model.Member;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Member operations.
 * This interface defines the business operations available for Member entities.
 * 
 * Migrated from the JBoss EAP Kitchensink application's MemberRegistration class.
 * This follows the Spring service pattern, separating the interface from implementation.
 */
public interface MemberService {

    /**
     * Register a new member.
     * 
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws Exception if registration fails (e.g., duplicate email)
     */
    Member register(Member member) throws Exception;
    
    /**
     * Find a member by ID.
     * 
     * @param id The ID of the member to find
     * @return Optional containing the member if found, empty otherwise
     */
    Optional<Member> findById(Long id);
    
    /**
     * Find a member by email.
     * 
     * @param email The email of the member to find
     * @return Optional containing the member if found, empty otherwise
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Get all members ordered by name.
     * 
     * @return List of all members ordered by name
     */
    List<Member> getAllMembersOrderedByName();
    
    /**
     * Update an existing member.
     * 
     * @param id The ID of the member to update
     * @param memberDetails The updated member details
     * @return The updated member
     * @throws Exception if update fails (e.g., member not found)
     */
    Member updateMember(Long id, Member memberDetails) throws Exception;
    
    /**
     * Delete a member by ID.
     * 
     * @param id The ID of the member to delete
     * @throws Exception if deletion fails (e.g., member not found)
     */
    void deleteMember(Long id) throws Exception;
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean emailExists(String email);
}
