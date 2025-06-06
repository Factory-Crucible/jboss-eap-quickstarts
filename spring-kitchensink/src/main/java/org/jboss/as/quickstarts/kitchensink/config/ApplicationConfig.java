/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.logging.Logger;

/**
 * Spring Boot configuration class for the kitchensink application.
 * Provides necessary bean definitions and configurations.
 */
@Configuration
@EnableWebMvc
public class ApplicationConfig implements WebMvcConfigurer {

    /**
     * Produces a Logger instance for injection.
     * This replicates the functionality of the CDI producer in the original application.
     * The logger name is derived from the class where it is injected.
     *
     * @param injectionPoint the point where the Logger is being injected
     * @return a Logger instance for the class where it's injected
     */
    @Bean
    @Scope("prototype")
    @Primary
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return Logger.getLogger(classOnWired.getName());
    }
    
    /**
     * Additional application configuration can be added here.
     * For example, message converters, view resolvers, etc.
     */
}
