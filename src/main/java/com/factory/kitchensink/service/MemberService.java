package com.factory.kitchensink.service;

import java.util.List;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.dto.MemberDTO;

/**
 * Service interface for managing members.
 * Defines the contract for member-related operations.
 */
public interface MemberService {
    
    /**
     * Registers a new member.
     * 
     * @param memberDTO the data for the member to register
     * @return the registered member as a DTO
     * @throws com.factory.kitchensink.exception.EmailAlreadyExistsException if a member with the same email already exists
     */
    MemberDTO register(CreateMemberDTO memberDTO);
    
    /**
     * Retrieves all members.
     * 
     * @return a list of all members as DTOs
     */
    List<MemberDTO> findAllMembers();
    
    /**
     * Finds a member by ID.
     * 
     * @param id the ID of the member to find
     * @return the member as a DTO
     * @throws com.factory.kitchensink.exception.MemberNotFoundException if no member is found with the given ID
     */
    MemberDTO findById(Long id);
    
    /**
     * Finds a member by email.
     * 
     * @param email the email of the member to find
     * @return the member as a DTO
     * @throws com.factory.kitchensink.exception.MemberNotFoundException if no member is found with the given email
     */
    MemberDTO findByEmail(String email);
}
