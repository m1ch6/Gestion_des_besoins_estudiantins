package com.university.sms.exception;

/**
 * Exception lancée lorsqu'une ressource n'est pas trouvée.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}