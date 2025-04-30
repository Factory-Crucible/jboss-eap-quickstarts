package com.example.kitchensink.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.kitchensink.event.MemberRegisteredEvent;

/**
 * Event listener for member-related events.
 * This class replaces the CDI event observer in the original JBoss application
 * that was used to update the member list when a new member was registered.
 * 
 * In Spring Boot, we use the @EventListener annotation to listen for
 * application events instead of CDI's @Observes.
 */
@Component
public class MemberEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(MemberEventListener.class);
    
    /**
     * Handles the MemberRegisteredEvent.
     * This method is called whenever a new member is registered.
     * 
     * In the original JBoss application, this functionality was in the
     * MemberListProducer.onMemberListChanged method, which used
     * @Observes(notifyObserver = Reception.IF_EXISTS).
     * 
     * Unlike the original application, we don't need to explicitly refresh
     * the member list here because Spring MVC controllers will request
     * the latest data from the service when needed, rather than relying
     * on a cached list produced by a CDI producer.
     * 
     * @param event the member registered event
     */
    @EventListener
    public void handleMemberRegisteredEvent(MemberRegisteredEvent event) {
        log.info("Received member registered event for: {}", event.getMember().getName());
        
        // In a more complex application, we might need to perform additional
        // actions here, such as notifying other components, updating caches,
        // or triggering additional business processes.
    }
}
