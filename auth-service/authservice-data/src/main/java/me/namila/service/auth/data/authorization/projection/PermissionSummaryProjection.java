package me.namila.service.auth.data.authorization.projection;

import java.util.UUID;

/**
 * Projection interface for Permission list views with minimal data.
 * Used when loading permissions associated with roles.
 */
public interface PermissionSummaryProjection {
    
    UUID getId();
    
    String getResource();
    
    String getAction();
    
    String getScope();
    
    String getDescription();
}

