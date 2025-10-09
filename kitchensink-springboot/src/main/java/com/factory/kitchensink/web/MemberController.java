package com.factory.kitchensink.web;

import com.factory.kitchensink.data.MemberRepository;
import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/rest/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {
    private final MemberRepository repository;
    private final MemberService service;

    public MemberController(MemberRepository repository, MemberService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<Member> listAllMembers() {
        return repository.findAll().stream()
                .sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        service.register(member);
        return ResponseEntity.ok().build();
    }
}
