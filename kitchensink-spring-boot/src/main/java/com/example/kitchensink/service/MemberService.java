package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link Member} entities.
 * This interface defines the operations that can be performed on members.
 */
public interface MemberService {

    /**
     * Retrieves all members.
     *
     * @return a list of all members
     */
    List<Member> findAllMembers();

    /**
     * Retrieves all members ordered by name.
     *
     * @return a list of all members ordered by name
     */
    List<Member> findAllMembersOrderedByName();

    /**
     * Retrieves a member by ID.
     *
     * @param id the ID of the member to retrieve
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findMemberById(Long id);

    /**
     * Retrieves a member by email address.
     *
     * @param email the email address of the member to retrieve
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findMemberByEmail(String email);

    /**
     * Creates a new member.
     *
     * @param member the member to create
     * @return the created member with generated ID
     * @throws jakarta.validation.ValidationException if the member is invalid
     * @throws IllegalArgumentException if a member with the same email already exists
     */
    Member createMember(Member member);

    /**
     * Updates an existing member.
     *
     * @param id the ID of the member to update
     * @param member the updated member data
     * @return the updated member
     * @throws jakarta.validation.ValidationException if the member is invalid
     * @throws IllegalArgumentException if the member does not exist or if the email is already in use by another member
     */
    Member updateMember(Long id, Member member);

    /**
     * Deletes a member by ID.
     *
     * @param id the ID of the member to delete
     * @throws IllegalArgumentException if the member does not exist
     */
    void deleteMember(Long id);

    /**
     * Checks if a member with the given email exists.
     *
     * @param email the email address to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean emailExists(String email);
}
