package com.migration.repository;

import com.migration.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find a member by email
    Member findByEmail(String email);

    // Find members by name (case insensitive)
    List<Member> findByNameIgnoreCase(String name);

    // Custom query to find members by phone number
    @Query("SELECT m FROM Member m WHERE m.phoneNumber = :phoneNumber")
    List<Member> findMembersByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    // Count members by name
    long countByName(String name);

    // Find members by name containing a certain string (case insensitive)
    List<Member> findByNameContainingIgnoreCase(String namePart);

    // Find members registered after a certain date
    @Query("SELECT m FROM Member m WHERE m.registrationDate > :date")
    List<Member> findMembersRegisteredAfter(@Param("date") java.util.Date date);
}