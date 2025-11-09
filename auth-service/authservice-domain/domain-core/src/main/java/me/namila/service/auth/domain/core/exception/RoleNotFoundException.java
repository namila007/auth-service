package me.namila.service.auth.domain.core.exception;

import java.util.UUID;

/**
 * Exception thrown when a role is not found.
 */
public class RoleNotFoundException extends DomainException {
    
    public RoleNotFoundException(UUID roleId) {
        super("ROLE_NOT_FOUND", "Role not found with ID: " + roleId);
    }
    
    public RoleNotFoundException(String roleName) {
        super("ROLE_NOT_FOUND", "Role not found with name: " + roleName);
    }
}

