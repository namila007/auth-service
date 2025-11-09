package me.namila.service.auth.domain.core.identity.service;

import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;

/**
 * Domain service for User aggregate root.
 * Contains business logic that doesn't naturally fit within the User aggregate.
 */
public class UserDomainService {
    
    /**
     * Validates if a user can be activated.
     * @param user The user to validate
     * @throws IllegalStateException if user cannot be activated
     */
    public void validateUserActivation(UserAggregate user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException("Cannot activate a suspended user");
        }
        
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new IllegalStateException("Cannot activate a locked user. Unlock first.");
        }
    }
    
    /**
     * Validates if username and email are unique.
     * This validation should be performed by the repository, but domain service
     * can provide additional business rules.
     * @param username The username to validate
     * @param email The email to validate
     */
    public void validateUniqueCredentials(UsernameValue username, EmailValue email) {
        if (username == null || username.getValue() == null || username.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (email == null || email.getValue() == null || email.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
    
    /**
     * Determines if a user can be deleted based on business rules.
     * @param user The user to check
     * @return true if user can be deleted
     */
    public boolean canDeleteUser(UserAggregate user) {
        if (user == null) {
            return false;
        }
        
        // Business rule: Cannot delete active users directly, must deactivate first
        return user.getStatus() != UserStatus.ACTIVE;
    }
}

