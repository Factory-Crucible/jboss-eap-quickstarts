package org.jboss.as.quickstarts.kitchensink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.logging.Logger;

/**
 * Configuration class that provides Logger instances.
 * This replaces the CDI @Inject Logger pattern from the original JBoss EAP application.
 */
@Configuration
public class LoggerConfig {

    /**
     * Produces a Logger instance for the class that injects it.
     * This mimics the behavior of CDI's @Inject Logger pattern.
     * 
     * @param injectionPoint the Spring injection point
     * @return a Logger instance named after the class that requested it
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint injectionPoint) {
        Class<?> clazz = injectionPoint.getMember().getDeclaringClass();
        return Logger.getLogger(clazz.getName());
    }
}
