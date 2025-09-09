package org.jboss.as.quickstarts.kitchensink.spring.web;

import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.spring.model.Member;
import org.jboss.as.quickstarts.kitchensink.spring.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.spring.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/members")
public class MemberRestController {

    private final MemberRepository repository;
    private final MemberService service;

    public MemberRestController(MemberRepository repository, MemberService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<Member> listAllMembers() {
        return repository.findAllByOrderByNameAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        service.register(member);
        return ResponseEntity.ok().build();
    }
}
