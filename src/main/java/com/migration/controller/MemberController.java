package com.migration.controller;

import com.migration.model.Member;
import com.migration.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member memberDetails) {
        return memberRepository.findById(id)
                .map(existingMember -> {
                    existingMember.setName(memberDetails.getName());
                    existingMember.setEmail(memberDetails.getEmail());
                    existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
                    Member updatedMember = memberRepository.save(existingMember);
                    return ResponseEntity.ok(updatedMember);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(member -> {
                    memberRepository.delete(member);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Member> getMemberByEmail(@PathVariable String email) {
        Member member = memberRepository.findByEmail(email);
        return member != null ? ResponseEntity.ok(member) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<Member> searchMembersByName(@RequestParam String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
    }
}