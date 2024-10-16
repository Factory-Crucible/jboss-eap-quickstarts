package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
}