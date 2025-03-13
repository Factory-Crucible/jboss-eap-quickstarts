package com.factory.kitchensink.controller;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.findAllOrderedByName();
    }

    @GetMapping("/{id}")
    public Member getMember(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            Member savedMember = memberService.register(member);
            return ResponseEntity.ok(savedMember);
        } catch (MemberEmailExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("email", "Email taken"));
        }
    }
}
