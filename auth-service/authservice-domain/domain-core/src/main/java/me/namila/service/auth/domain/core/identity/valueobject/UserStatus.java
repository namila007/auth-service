package me.namila.service.auth.domain.core.identity.valueobject;

/**
 * User status value object representing the state of a user account.
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    LOCKED,
    PENDING_VERIFICATION
}

