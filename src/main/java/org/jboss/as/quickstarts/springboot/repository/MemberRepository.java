package org.jboss.as.quickstarts.springboot.repository;

import org.jboss.as.quickstarts.springboot.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     *
     * @param email the email address to search for
     * @return the member with the given email address
     */
    Member findByEmail(String email);

    /**
     * Find all members ordered by name.
     *
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}