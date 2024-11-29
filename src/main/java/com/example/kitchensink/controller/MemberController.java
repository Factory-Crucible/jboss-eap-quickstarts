package com.example.kitchensink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.kitchensink.service.MemberService;
import com.example.kitchensink.data.MemberRepository;
import com.example.kitchensink.model.Member;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Handles RESTful requests for managing Member entities.
 */
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final Validator validator;

    @Autowired
    public MemberController(MemberService memberService, MemberRepository memberRepository, Validator validator) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<List<Member>> listAllMembers() {
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            return new ResponseEntity<>(createViolationResponse(violations), HttpStatus.BAD_REQUEST);
        }

        if (emailAlreadyExists(member.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        try {
            memberRepository.save(member);
            return new ResponseEntity<>(member, HttpStatus.CREATED);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(createViolationResponse(e.getConstraintViolations()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/existsByEmail/{email}")
    public ResponseEntity<Boolean> emailAlreadyExists(@PathVariable("email") String email) {
        Member existingMember = memberRepository.findByEmail(email);
        return new ResponseEntity<>(existingMember != null, HttpStatus.OK);
    }

    private Map<String, String> createViolationResponse(Set<? extends ConstraintViolation<?>> violations) {
        Map<String, String> response = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            response.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return response;
    }
}
