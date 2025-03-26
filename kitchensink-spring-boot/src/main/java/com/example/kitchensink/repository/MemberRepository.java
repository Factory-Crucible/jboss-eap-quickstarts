package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * This interface provides CRUD operations and custom queries for the Member entity.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);

    /**
     * Check if a member with the given email exists.
     *
     * @param email the email address to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all members ordered by name in ascending order.
     *
     * @return a list of all members ordered by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}
