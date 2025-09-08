package com.factory.kitchensink.service;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> listAll() {
        return repository.findAll();
    }

    public Member getById(@NotNull Long id) {
        return repository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
    }

    @Transactional
    public Member register(@Valid Member member) {
        if (repository.existsByEmail(member.getEmail())) {
            throw new DuplicateEmailException(member.getEmail());
        }
        try {
            return repository.save(member);
        } catch (DataIntegrityViolationException e) {
            // In case of race condition with the unique constraint
            throw new DuplicateEmailException(member.getEmail());
        }
    }

    public static class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(Long id) {
            super("Member not found: " + id);
        }
    }

    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String email) {
            super("Email already registered: " + email);
        }
    }
}
