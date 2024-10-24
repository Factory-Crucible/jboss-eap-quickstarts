package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Member} entities.
 * This interface provides CRUD operations and custom queries for Member entities.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by their email address.
     *
     * @param email the email address to search for
     * @return the Member with the given email, or null if not found
     */
    Member findByEmail(String email);

    /**
     * Retrieve all members ordered by name.
     *
     * @return a list of all members, sorted alphabetically by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name")
    List<Member> findAllOrderByName();
}