package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * This interface provides methods to perform CRUD operations and custom queries
 * on Member entities using Spring Data JPA.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by their email address.
     * 
     * @param email The email address to search for
     * @return An Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find members whose names contain the given substring (case-insensitive).
     * 
     * @param name The substring to search for in member names
     * @return A list of members whose names contain the given substring
     */
    List<Member> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email The email address to check
     * @return true if a member with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Custom query to search for members by name or email.
     * This query searches for members whose name or email contains the given search term.
     * 
     * @param searchTerm The term to search for in member names or emails
     * @param pageable Pagination information
     * @return A page of members matching the search criteria
     */
    @Query("SELECT m FROM Member m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Member> searchMembers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find members by phone number.
     * 
     * @param phoneNumber The phone number to search for
     * @return A list of members with the given phone number
     */
    List<Member> findByPhoneNumber(String phoneNumber);
    
    /**
     * Count members with a name containing the given substring (case-insensitive).
     * 
     * @param name The substring to search for in member names
     * @return The number of members whose names contain the given substring
     */
    long countByNameContainingIgnoreCase(String name);
}
