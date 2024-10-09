package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class MemberService {

    private final Logger log = Logger.getLogger(MemberService.class.getName());

    private final MemberRepository memberRepository;
    private final Validator validator;

    @Autowired
    public MemberService(MemberRepository memberRepository, Validator validator) {
        this.memberRepository = memberRepository;
        this.validator = validator;
    }

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        // Validate the member using Bean Validation
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            // Handle unique constraint violation
            if (e.getCause() != null && e.getCause().getCause() != null
                && e.getCause().getCause().getMessage().contains("unique constraint")
                || e.getCause().getCause().getMessage().contains("ConstraintViolationException")) {
                throw new Exception("Email already exists: " + member.getEmail());
            } else {
                // Handle other exceptions
                throw e;
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    @Transactional
    public void update(Member member) {
        if (!memberRepository.existsById(member.getId())) {
            throw new RuntimeException("Member not found with id: " + member.getId());
        }
        memberRepository.save(member);
    }

    @Transactional
    public void delete(String id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}