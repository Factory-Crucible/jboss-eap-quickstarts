package org.jboss.eap.quickstarts.kitchensink.repository;

import org.jboss.eap.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Member entities.
 * This interface provides CRUD operations and custom queries for Member entities.
 * 
 * Migrated from the JBoss EAP Kitchensink application's MemberRepository class.
 * Spring Data JPA automatically implements the methods based on method names
 * and annotations, replacing the manual implementation in the original application.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     * 
     * @param email The email address to search for
     * @return Optional containing the member if found, empty otherwise
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email The email address to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all members ordered by name.
     * 
     * @return List of all members ordered by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
    
    /**
     * Find members whose name contains the given string (case insensitive).
     * 
     * @param name The name pattern to search for
     * @return List of members matching the name pattern
     */
    List<Member> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find members by phone number.
     * 
     * @param phoneNumber The phone number to search for
     * @return List of members with the given phone number
     */
    List<Member> findByPhoneNumber(String phoneNumber);
    
    /**
     * Count members with the given email domain.
     * 
     * @param domain The email domain to search for (e.g., "example.com")
     * @return The number of members with email addresses in the given domain
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.email LIKE %:domain")
    long countByEmailDomain(@Param("domain") String domain);
}
