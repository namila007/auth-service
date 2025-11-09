package me.namila.service.auth.data.identity.projection;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Projection interface for User list views with minimal data.
 * Used to avoid N+1 queries and reduce data transfer.
 */
public interface UserSummaryProjection {
    
    UUID getId();
    
    String getUsername();
    
    String getEmail();
    
    String getStatus();
    
    LocalDateTime getCreatedAt();
    
    LocalDateTime getUpdatedAt();
}

