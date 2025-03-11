package org.jboss.as.quickstarts.kitchensink.util;

/**
 * Utility class for extracting readable error messages from exceptions.
 */
public class ErrorMessageExtractor {

    private static final String DEFAULT_ERROR_MESSAGE = 
        "Registration failed. See server log for more information";

    /**
     * Extracts the root cause message from an exception chain.
     * Traverses the exception hierarchy to find the most specific error message.
     *
     * @param e The exception to analyze
     * @return The most specific error message available
     */
    public static String getRootErrorMessage(Exception e) {
        if (e == null) {
            return DEFAULT_ERROR_MESSAGE;
        }

        // Start with the exception and recurse to find the root cause
        Throwable rootCause = e;
        String errorMessage = DEFAULT_ERROR_MESSAGE;

        while (rootCause != null) {
            String localizedMessage = rootCause.getLocalizedMessage();
            if (localizedMessage != null && !localizedMessage.isEmpty()) {
                errorMessage = localizedMessage;
            }
            rootCause = rootCause.getCause();
        }

        return errorMessage;
    }
}
