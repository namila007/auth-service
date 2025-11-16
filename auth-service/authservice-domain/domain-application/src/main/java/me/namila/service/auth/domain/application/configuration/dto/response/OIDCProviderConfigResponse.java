package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for OIDC provider configuration (basic summary).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCProviderConfigResponse {
    
    private UUID providerId;
    private String providerName;
    private String displayName;
    private String providerType;
    private Boolean enabled;
    private Instant createdAt;
    private Instant lastModifiedAt;
    
    // Note: Configuration details excluded for security in summary view
    private Map<String, Object> metadata;
}
