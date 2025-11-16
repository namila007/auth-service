package me.namila.service.auth.domain.core.exception;

import java.util.UUID;

/**
 * Exception thrown when a resource is not found.
 * Generic exception for any entity/aggregate that cannot be found.
 */
public class ResourceNotFoundException extends DomainException {
    
    public ResourceNotFoundException(String resourceType, UUID resourceId) {
        super("RESOURCE_NOT_FOUND", 
              resourceType + " not found with ID: " + resourceId);
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super("RESOURCE_NOT_FOUND", 
              resourceType + " not found: " + identifier);
    }
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}
