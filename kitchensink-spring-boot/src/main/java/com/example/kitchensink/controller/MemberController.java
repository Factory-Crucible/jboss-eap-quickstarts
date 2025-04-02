package com.example.kitchensink.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

import jakarta.validation.Valid;

/**
 * REST controller for managing {@link Member} resources.
 * This controller provides endpoints for retrieving, creating, and managing members.
 */
@RestController
@RequestMapping("/rest/members")
public class MemberController {

    private Logger log = Logger.getLogger(MemberController.class.getName());
    
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    /**
     * GET /rest/members : Get all members.
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        log.info("REST request to get all Members");
        List<Member> members = memberService.getAllMembersOrderedByName();
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /rest/members/{id} : Get the member with the specified ID.
     * 
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        log.info("REST request to get Member with ID: " + id);
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + id));
    }
    
    /**
     * POST /rest/members : Create a new member.
     * 
     * @param member the member to create
     * @return the ResponseEntity with status 200 (OK),
     *         or with status 400 (Bad Request) if the member has validation errors,
     *         or with status 409 (Conflict) if the email is already in use
     */
    @PostMapping
    public ResponseEntity<Object> createMember(@Valid @RequestBody Member member) {
        log.info("REST request to save Member: " + member);
        
        try {
            Member result = memberService.register(member);
            return ResponseEntity.ok().build();
        } catch (EmailAlreadyExistsException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseObj);
        }
    }
    
    /**
     * Exception handler for validation errors.
     * 
     * @param ex the exception
     * @return a map of field errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
