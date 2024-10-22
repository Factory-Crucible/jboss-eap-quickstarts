package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find a member by email
    Optional<Member> findByEmail(String email);

    // Find members by name (assuming partial match)
    List<Member> findByNameContainingIgnoreCase(String name);

    // Count members by email domain
    long countByEmailEndingWith(String emailDomain);

    // Find members by registration date range
    List<Member> findByRegistrationDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Custom query to find members with a specific phone number area code
    List<Member> findByPhoneNumberStartingWith(String areaCode);
}