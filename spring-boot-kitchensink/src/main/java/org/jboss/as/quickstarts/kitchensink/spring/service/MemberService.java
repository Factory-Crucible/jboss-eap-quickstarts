package org.jboss.as.quickstarts.kitchensink.spring.service;

import org.jboss.as.quickstarts.kitchensink.spring.model.Member;
import org.jboss.as.quickstarts.kitchensink.spring.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void register(Member member) {
        repository.findByEmail(member.getEmail()).ifPresent(m -> {
            throw new UniqueEmailViolation("Email taken");
        });
        repository.save(member);
    }

    public static class UniqueEmailViolation extends RuntimeException {
        public UniqueEmailViolation(String message) {
            super(message);
        }
    }
}
