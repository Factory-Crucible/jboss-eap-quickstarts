package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String showRegistrationForm(Model model) {
        model.addAttribute("newMember", new Member());
        return "index";
    }

    @PostMapping("/register")
    public String registerMember(@Valid Member newMember, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "index";
        }

        try {
            memberService.register(newMember);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful");
            return "redirect:/";
        } catch (Exception e) {
            log.error("Error registering new member", e);
            model.addAttribute("errorMessage", "Error registering new member: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/members")
    public String listMembers(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "members";
    }

    @GetMapping("/rest/members")
    public String listMembersRest(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "members-rest";
    }
}