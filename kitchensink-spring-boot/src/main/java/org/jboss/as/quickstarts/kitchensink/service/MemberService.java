package org.jboss.as.quickstarts.kitchensink.service;

import lombok.RequiredArgsConstructor;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;

    public List<Member> listAllOrderedByName() {
        return repository.findAllByOrderByNameAsc();
    }

    public Optional<Member> findById(Long id) {
        return repository.findById(id);
    }

    public boolean emailAlreadyExists(String email) {
        return repository.existsByEmail(email);
    }

    public Member register(Member member) {
        return repository.save(member);
    }
}
