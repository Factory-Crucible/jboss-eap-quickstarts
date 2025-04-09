package com.example.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.kitchensink.model.Member;

/**
 * Repository for Member entities.
 * 
 * This interface extends Spring Data JPA's JpaRepository to provide CRUD operations
 * and custom query methods for the Member entity. It replaces the original JBoss
 * MemberRepository class with a more concise Spring Data approach.
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
     * Find all members ordered by name.
     * 
     * @return A list of all members, sorted alphabetically by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}
