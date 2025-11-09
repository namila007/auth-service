package me.namila.service.auth.domain.core.exception;

import java.util.UUID;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends DomainException {
    
    public UserNotFoundException(UUID userId) {
        super("USER_NOT_FOUND", "User not found with ID: " + userId);
    }
    
    public UserNotFoundException(String username) {
        super("USER_NOT_FOUND", "User not found with username: " + username);
    }
}

