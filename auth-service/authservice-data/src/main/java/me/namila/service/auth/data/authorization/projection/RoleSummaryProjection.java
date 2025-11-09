package me.namila.service.auth.data.authorization.projection;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Projection interface for Role list views with minimal data.
 * Used to avoid N+1 queries when loading roles.
 */
public interface RoleSummaryProjection {
    
    UUID getId();
    
    String getRoleName();
    
    String getDisplayName();
    
    String getRoleType();
    
    LocalDateTime getCreatedAt();
}

