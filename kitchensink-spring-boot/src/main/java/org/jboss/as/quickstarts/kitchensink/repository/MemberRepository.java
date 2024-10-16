package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Finds a member by email.
     *
     * @param email the email to search for
     * @return the member with the given email, or null if not found
     */
    Member findByEmail(String email);

    /**
     * Finds all members ordered by name.
     *
     * @return a list of all members, ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}