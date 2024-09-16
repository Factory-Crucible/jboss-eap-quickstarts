package com.example.kitchensink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberRegistration;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

/**
 * Spring MVC Controller for handling Member-related operations.
 * This class is adapted from the original JBoss MemberController to work with Spring Boot.
 *
 * The @RestController stereotype is a convenience mechanism to create a RESTful controller.
 */
@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberRegistration memberRegistration;

    private Member newMember;

    @PostConstruct
    public void initNewMember() {
        newMember = new Member();
    }

    /**
     * Registers a new member.
     * This method has been adapted from the original JSF-based implementation to a RESTful endpoint.
     *
     * @param member The member to be registered
     * @return ResponseEntity with a success message or error details
     */
    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody Member member) {
        try {
            memberRegistration.register(member);
            return ResponseEntity.ok("Registration successful");
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration unsuccessful: " + errorMessage);
        }
    }

    /**
     * Helper method to extract the root cause of an exception.
     * This method is preserved from the original implementation.
     *
     * @param e The exception to analyze
     * @return The root error message
     */
    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }
}