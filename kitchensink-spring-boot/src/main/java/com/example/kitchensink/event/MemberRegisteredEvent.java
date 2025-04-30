package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;
import java.time.LocalDateTime;

/**
 * Event that is published when a new member is registered.
 * This replaces the CDI event firing in the original JBoss application.
 */
public class MemberRegisteredEvent {

    private final Member member;
    private final LocalDateTime timestamp;

    /**
     * Creates a new member registered event.
     *
     * @param member the member that was registered
     */
    public MemberRegisteredEvent(Member member) {
        this.member = member;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Gets the member that was registered.
     *
     * @return the registered member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Gets the timestamp when the event was created.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "MemberRegisteredEvent{" +
                "member=" + member +
                ", timestamp=" + timestamp +
                '}';
    }
}
