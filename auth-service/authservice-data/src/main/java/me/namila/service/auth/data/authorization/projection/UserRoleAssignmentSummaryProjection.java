package me.namila.service.auth.data.authorization.projection;

import java.time.Instant;
import java.util.UUID;

/**
 * Projection interface for UserRoleAssignment list views with minimal data.
 * Used when loading role assignments for a user.
 */
public interface UserRoleAssignmentSummaryProjection {
    
    UUID getId();
    
    UUID getUserId();
    
    UUID getRoleId();
    
    String getRoleName();
    
    String getScope();
    
    String getScopeContext();
    
    Instant getEffectiveFrom();
    
    Instant getEffectiveUntil();
    
    String getStatus();
}

