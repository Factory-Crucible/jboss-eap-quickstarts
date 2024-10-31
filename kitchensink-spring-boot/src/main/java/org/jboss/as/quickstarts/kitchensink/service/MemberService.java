package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class MemberService {

    @Autowired
    private Logger log;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Validator validator;

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        // Validate the member entity
        validateMember(member);

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new Exception("Error registering member", e);
        }

        log.info(member.getName() + " was registered successfully");
    }

    private void validateMember(Member member) throws ConstraintViolationException {
        // Validate the entity
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Invalid member", violations);
        }
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Iterable<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }
}