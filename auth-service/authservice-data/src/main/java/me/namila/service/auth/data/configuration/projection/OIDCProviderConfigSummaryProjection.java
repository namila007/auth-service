package me.namila.service.auth.data.configuration.projection;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data projection for OIDCProviderConfig summary (minimal data).
 */
public interface OIDCProviderConfigSummaryProjection {
    
    UUID getProviderId();
    
    String getProviderName();
    
    String getDisplayName();
    
    String getProviderType();
    
    Boolean getEnabled();
    
    Instant getCreatedAt();
    
    Instant getLastModifiedAt();
}

