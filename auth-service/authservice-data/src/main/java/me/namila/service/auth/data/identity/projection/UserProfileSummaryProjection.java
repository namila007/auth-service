package me.namila.service.auth.data.identity.projection;

import java.util.UUID;

/**
 * Projection interface for UserProfile with minimal data.
 * Used when loading user profile information without full user details.
 */
public interface UserProfileSummaryProjection {
    
    UUID getId();
    
    String getFirstName();
    
    String getLastName();
    
    String getDisplayName();
}

