package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;

/**
 * Event that is published when a new member is registered.
 * This event can be consumed by event listeners to perform additional actions
 * after a member registration.
 */
public class MemberRegisteredEvent {

    private final Member member;

    /**
     * Creates a new member registered event.
     *
     * @param member the member that was registered
     */
    public MemberRegisteredEvent(Member member) {
        this.member = member;
    }

    /**
     * Gets the registered member.
     *
     * @return the member that was registered
     */
    public Member getMember() {
        return member;
    }
}
