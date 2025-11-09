package me.namila.service.auth.domain.core.exception;

/**
 * Exception thrown when attempting to create a duplicate entity.
 */
public class DuplicateEntityException extends DomainException {
    
    public DuplicateEntityException(String entityType, String field, String value) {
        super("DUPLICATE_ENTITY", String.format("%s with %s '%s' already exists", entityType, field, value));
    }
}

