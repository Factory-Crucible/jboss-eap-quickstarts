package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;

/**
 * Event that is published when a new member is registered.
 * This replaces the CDI Event<Member> mechanism used in the original JBoss application.
 * 
 * Spring's event system works by publishing instances of event classes like this one,
 * which can then be consumed by event listeners using @EventListener.
 */
public class MemberRegisteredEvent {
    
    private final Member member;
    
    /**
     * Creates a new event for the given member.
     * 
     * @param member the newly registered member
     */
    public MemberRegisteredEvent(Member member) {
        this.member = member;
    }
    
    /**
     * Gets the member associated with this event.
     * 
     * @return the newly registered member
     */
    public Member getMember() {
        return member;
    }
}
