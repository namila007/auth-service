package me.namila.service.auth.data.identity.projection;

import java.time.Instant;
import java.util.UUID;

/**
 * Projection interface for FederatedIdentity list views with minimal data.
 * Used when loading federated identities associated with a user.
 */
public interface FederatedIdentitySummaryProjection {
    
    UUID getId();
    
    UUID getProviderId();
    
    String getSubjectId();
    
    String getIssuer();
    
    Instant getLinkedAt();
}

