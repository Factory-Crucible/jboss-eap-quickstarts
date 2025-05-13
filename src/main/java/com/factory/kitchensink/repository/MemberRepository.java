package com.factory.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factory.kitchensink.model.Member;

/**
 * Repository interface for Member entity.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email the email to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find members whose names contain the given string, ordered by name.
     * 
     * @param name the name substring to search for
     * @return a list of members matching the criteria, ordered by name
     */
    List<Member> findByNameContainingOrderByName(String name);
}
