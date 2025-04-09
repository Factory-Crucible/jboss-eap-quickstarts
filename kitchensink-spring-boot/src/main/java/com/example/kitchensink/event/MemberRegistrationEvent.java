package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;
import lombok.Getter;

/**
 * Event that is published when a new member is registered.
 * 
 * This class replaces the CDI event mechanism used in the original JBoss application.
 * In Spring, we use the ApplicationEventPublisher to publish events and
 * methods annotated with @EventListener to consume them.
 */
@Getter
public class MemberRegistrationEvent {
    
    private final Member member;
    
    /**
     * Creates a new MemberRegistrationEvent.
     * 
     * @param member The member that was registered
     */
    public MemberRegistrationEvent(Member member) {
        this.member = member;
    }
}
