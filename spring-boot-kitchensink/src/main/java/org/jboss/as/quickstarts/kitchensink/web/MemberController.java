package org.jboss.as.quickstarts.kitchensink.web;

import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class MemberController {

    private final MemberRepository repository;
    private final MemberService service;

    public MemberController(MemberRepository repository, MemberService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(path = "/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> listAllMembers() {
        return repository.findAllByOrderByNameAsc();
    }

    @GetMapping(path = "/members/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Member lookupMemberById(@PathVariable("id") long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }

    @PostMapping(path = "/members", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        service.register(member);
        return ResponseEntity.ok().build();
    }
}
