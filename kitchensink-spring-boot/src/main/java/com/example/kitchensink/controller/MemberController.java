package com.example.kitchensink.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

/**
 * Controller for web interface to manage members.
 * This class replaces the JSF MemberController from the JBoss application.
 * 
 * It handles requests for displaying the member registration form,
 * processing form submissions, and displaying the list of members.
 */
@Controller
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    
    private final MemberService memberService;
    
    /**
     * Creates a new MemberController with the given dependencies.
     * 
     * @param memberService service for member operations
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    /**
     * Handles GET requests to the home page.
     * This displays the registration form and the list of members.
     * 
     * @param model the model to add attributes to
     * @return the view name
     */
    @GetMapping("/")
    public String home(Model model) {
        // Initialize a new member if one doesn't exist in the model
        if (!model.containsAttribute("newMember")) {
            model.addAttribute("newMember", new Member());
        }
        
        // Add the list of members to the model
        model.addAttribute("members", memberService.getAllMembers());
        
        return "index";
    }
    
    /**
     * Handles POST requests to register a new member.
     * This validates the member data and registers the member if valid.
     * 
     * @param newMember the member data from the form
     * @param result binding result for validation errors
     * @param redirectAttributes attributes for the redirect
     * @return redirect to the home page
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("newMember") Member newMember, 
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        // If there are validation errors, add them to the redirect
        if (result.hasErrors()) {
            log.info("Validation errors in member registration: {}", result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newMember", result);
            redirectAttributes.addFlashAttribute("newMember", newMember);
            return "redirect:/";
        }
        
        try {
            // Register the member
            memberService.register(newMember);
            // Add success message
            redirectAttributes.addFlashAttribute("message", "Registration successful!");
            log.info("Member registered successfully: {}", newMember.getName());
        } catch (Exception e) {
            // Add error message
            String errorMessage = getRootErrorMessage(e);
            log.error("Error registering member: {}", errorMessage);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            redirectAttributes.addFlashAttribute("newMember", newMember);
        }
        
        return "redirect:/";
    }
    
    /**
     * Gets the root error message from an exception.
     * This recursively traverses the exception chain to find the root cause.
     * 
     * @param e the exception
     * @return the root error message
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
