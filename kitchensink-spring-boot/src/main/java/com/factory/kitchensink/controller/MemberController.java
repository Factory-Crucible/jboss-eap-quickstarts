package com.factory.kitchensink.controller;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    public List<Member> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Member getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Member> create(@Valid @RequestBody Member member) {
        Member saved = service.register(member);
        return ResponseEntity.created(URI.create("/api/members/" + saved.getId())).body(saved);
    }
}
