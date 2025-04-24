package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is published when a new member is registered.
 * 
 * This event extends Spring's ApplicationEvent and carries the
 * newly registered Member entity as its source.
 */
public class MemberRegisteredEvent extends ApplicationEvent {
    
    /**
     * Creates a new MemberRegisteredEvent.
     * 
     * @param member The newly registered Member entity
     */
    public MemberRegisteredEvent(Member member) {
        super(member);
    }
    
    /**
     * Returns the Member entity that was registered.
     * 
     * @return The registered Member entity
     */
    public Member getMember() {
        return (Member) getSource();
    }
}
