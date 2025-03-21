package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;
import lombok.Getter;

@Getter
public class MemberRegisteredEvent {
    private final Member member;

    public MemberRegisteredEvent(Member member) {
        this.member = member;
    }
}
