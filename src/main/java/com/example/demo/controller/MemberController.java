package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.MemberService;
import com.example.demo.model.Member;
import java.util.List;

/**
 * A Spring REST controller for handling member-related operations.
 * <p>
 * This controller provides endpoints for creating, retrieving, updating, and deleting members.
 * It replaces the functionality previously provided by the JaxRsActivator and MemberResource classes
 * in the JBoss EAP application.
 * </p>
 */
@RestController
@RequestMapping("/rest/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * Creates a new member.
     *
     * @param member The Member to create
     * @return The created Member
     */
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    /**
     * Lists all members.
     *
     * @return A list of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.getAllMembers();
    }

    /**
     * Looks up a member by id.
     *
     * @param id The id of the Member to be retrieved
     * @return The Member with the specified id
     */
    @GetMapping("/{id}")
    public Member lookupMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    /**
     * Updates an existing member.
     *
     * @param id The id of the Member to be updated
     * @param member The updated Member information
     * @return The updated Member
     */
    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    /**
     * Deletes a member by id.
     *
     * @param id The id of the Member to be deleted
     */
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }
}