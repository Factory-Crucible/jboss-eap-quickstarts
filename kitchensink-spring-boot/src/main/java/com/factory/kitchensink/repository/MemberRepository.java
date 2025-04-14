package com.factory.kitchensink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.factory.kitchensink.model.Member;

/**
 * Repository for {@link Member} entities.
 * Provides methods to interact with the database.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email the email to search for
     * @return the member with the specified email
     */
    Member findByEmail(String email);
    
    /**
     * Find all members ordered by name.
     * 
     * @return list of all members ordered by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}
