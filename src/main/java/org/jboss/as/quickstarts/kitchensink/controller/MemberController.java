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
package org.jboss.as.quickstarts.kitchensink.controller;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensink.util.ErrorMessageExtractor;

/**
 * Controller responsible for handling member registration operations.
 * This class serves as a request-scoped bean that manages the lifecycle
 * of member registration requests and associated UI interactions.
 */
@Model
public class MemberController {

    private final FacesContext facesContext;
    private final MemberRegistration memberRegistration;
    private Member newMember;

    /**
     * Constructor for dependency injection.
     * @param facesContext Context for JSF operations
     * @param memberRegistration Service for member registration operations
     */
    public MemberController(FacesContext facesContext, MemberRegistration memberRegistration) {
        this.facesContext = facesContext;
        this.memberRegistration = memberRegistration;
    }

    /**
     * Initializes a new Member instance for registration.
     * Called after dependency injection is complete.
     */
    @PostConstruct
    public void initNewMember() {
        newMember = new Member();
    }

    /**
     * Produces a named member instance for JSF binding.
     * @return new member instance for registration form
     */
    @Produces
    @Named
    public Member getNewMember() {
        return newMember;
    }

    /**
     * Handles the member registration process.
     * Attempts to register a new member and provides appropriate feedback messages.
     */
    public void register() {
        try {
            memberRegistration.register(newMember);
            displaySuccessMessage();
            initNewMember();
        } catch (Exception e) {
            handleRegistrationError(e);
        }
    }

    /**
     * Displays a success message after successful registration.
     */
    private void displaySuccessMessage() {
        FacesMessage m = new FacesMessage(
            FacesMessage.SEVERITY_INFO,
            "Registered!",
            "Registration successful"
        );
        facesContext.addMessage(null, m);
    }

    /**
     * Handles registration errors by displaying appropriate error messages.
     * @param e Exception thrown during registration
     */
    private void handleRegistrationError(Exception e) {
        String errorMessage = ErrorMessageExtractor.getRootErrorMessage(e);
        FacesMessage m = new FacesMessage(
            FacesMessage.SEVERITY_ERROR,
            errorMessage,
            "Registration unsuccessful"
        );
        facesContext.addMessage(null, m);
    }
}
