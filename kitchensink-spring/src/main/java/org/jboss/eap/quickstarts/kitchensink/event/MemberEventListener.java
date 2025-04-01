package org.jboss.eap.quickstarts.kitchensink.event;

import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.kitchensink.model.Member;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for Member events.
 * This class listens for Member events published by the MemberService.
 * 
 * Migrated from the JBoss EAP Kitchensink application's CDI event handling.
 * The original CDI event observer method has been replaced with Spring's @EventListener.
 */
@Component
@Slf4j
public class MemberEventListener {

    /**
     * Handles Member events.
     * This method is called whenever a Member object is published as an event.
     * It logs the event and could trigger additional actions like refreshing caches,
     * sending notifications, or updating related data.
     * 
     * @param member The Member that was created, updated, or deleted
     */
    @EventListener
    public void onMemberEvent(Member member) {
        log.info("Received member event for: {}", member.getName());
        
        // Additional event handling logic can be added here
        // For example:
        // - Update caches
        // - Send notifications
        // - Trigger related data updates
        // - Publish metrics
    }
    
    /**
     * Specialized handler for member registration events.
     * This method demonstrates how to use conditional event handling.
     * 
     * @param member The Member that was registered
     * @param registrationEvent The registration event annotation (if present)
     */
    @EventListener(condition = "#registrationEvent != null")
    public void onMemberRegistration(Member member, MemberRegistrationEvent registrationEvent) {
        log.info("New member registered: {}", member.getName());
        
        // Specific logic for new member registrations
    }
    
    /**
     * Marker annotation for member registration events.
     * This can be used to differentiate between different types of member events.
     */
    public @interface MemberRegistrationEvent {
    }
}
