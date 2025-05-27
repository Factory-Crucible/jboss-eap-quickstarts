package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Member entities.
 * This interface extends Spring Data JPA's JpaRepository to provide
 * CRUD operations and custom query methods for Member entities.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);

    /**
     * Find all members ordered by name.
     *
     * @return a list of all members, sorted alphabetically by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name")
    List<Member> findAllOrderedByName();
}
