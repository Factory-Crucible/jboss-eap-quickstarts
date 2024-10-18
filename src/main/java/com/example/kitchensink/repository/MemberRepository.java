package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find a member by email
    Member findByEmail(String email);

    // Find members by name (assuming partial match)
    List<Member> findByNameContainingIgnoreCase(String name);

    // Find members by phone number
    List<Member> findByPhoneNumber(String phoneNumber);

    // Custom query to find members by name or email
    List<Member> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    // You can add more custom query methods as needed
}