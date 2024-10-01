package com.example.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.springboot.model.Member;

import java.util.List;

/**
 * Spring Data JPA repository for the Member entity.
 * This class replaces the original MemberRepository and uses Spring Data JPA features.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by their email address.
     * This method uses Spring Data JPA's method name query creation feature.
     *
     * @param email the email address to search for
     * @return the Member with the given email address
     */
    Member findByEmail(String email);

    /**
     * Find all members ordered by name.
     * This method uses a custom JPQL query to order the results.
     *
     * @return a list of all Members ordered by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();

    // Note: The findById method is automatically provided by JpaRepository
    // and does not need to be explicitly defined here.

    /*
     * The original comments about type-safe criteria queries have been removed
     * as they are not applicable in the Spring Data JPA context. Spring Data JPA
     * provides its own mechanisms for type-safe queries, such as the Criteria API
     * or QueryDSL integration, which can be used if needed in more complex scenarios.
     */
}