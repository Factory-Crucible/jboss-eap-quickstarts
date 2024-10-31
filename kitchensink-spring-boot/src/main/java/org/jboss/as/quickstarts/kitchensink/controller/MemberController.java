package org.jboss.as.quickstarts.kitchensink.controller;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/")
    public String home(Model model) {
        List<Member> members = memberService.findAllOrderedByName();
        model.addAttribute("members", members);
        model.addAttribute("newMember", new Member());
        return "index";
    }

    @PostMapping("/register")
    public String registerMember(@Valid @ModelAttribute("newMember") Member newMember,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Member> members = memberService.findAllOrderedByName();
            model.addAttribute("members", members);
            return "index";
        }

        try {
            memberService.register(newMember);
            redirectAttributes.addFlashAttribute("successMessage", "Member registered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering member: " + e.getMessage());
        }

        return "redirect:/";
    }

    @ModelAttribute("members")
    public List<Member> getMembers() {
        return memberService.findAllOrderedByName();
    }
}