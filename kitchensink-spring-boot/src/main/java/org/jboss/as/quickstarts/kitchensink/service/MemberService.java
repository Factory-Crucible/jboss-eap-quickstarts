package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final Validator validator;

    @Autowired
    public MemberService(MemberRepository memberRepository, Validator validator) {
        this.memberRepository = memberRepository;
        this.validator = validator;
    }

    @Transactional
    public void register(Member member) throws ConstraintViolationException {
        validateMember(member);

        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        memberRepository.save(member);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public void update(Member member) {
        validateMember(member);
        memberRepository.save(member);
    }

    @Transactional
    public void delete(Long id) {
        Member member = findById(id);
        memberRepository.delete(member);
    }

    private void validateMember(Member member) {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}