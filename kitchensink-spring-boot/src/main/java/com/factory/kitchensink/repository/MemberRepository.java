package com.factory.kitchensink.repository;

import com.factory.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Member entity.
 * 
 * This is a migration of the original JBoss EAP kitchensink MemberRepository.
 * The original repository used EntityManager with Criteria API for queries, while
 * this version leverages Spring Data JPA's method naming conventions for automatic
 * query generation.
 * 
 * Migration mapping:
 * - Original: EntityManager with manual queries
 * - Spring Boot: JpaRepository with method name conventions
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * Replaces the original findByEmail method that used Criteria API.
     * 
     * @param email the email address to search for
     * @return the member with the specified email or null if not found
     */
    Member findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * Replaces the original findAllOrderedByName method that used Criteria API.
     * 
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
