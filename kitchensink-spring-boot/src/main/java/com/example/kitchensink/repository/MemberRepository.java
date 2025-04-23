package com.example.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.kitchensink.model.Member;

/**
 * Repository for Member entities.
 * Provides methods for accessing and manipulating Member data.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the member if found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name.
     * 
     * @return a list of all members ordered by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}
