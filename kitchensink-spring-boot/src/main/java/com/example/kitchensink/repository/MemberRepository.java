package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find a member by email
    Member findByEmail(String email);

    // Find members by name (case-insensitive, containing the given string)
    List<Member> findByNameContainingIgnoreCase(String name);

    // Find members by phone number
    List<Member> findByPhoneNumber(String phoneNumber);

    // Count members by email domain
    long countByEmailEndingWith(String emailDomain);

    // Find members registered after a certain date
    List<Member> findByRegistrationDateAfter(java.time.LocalDateTime date);

    // Find members by name and email
    Member findByNameAndEmail(String name, String email);
}