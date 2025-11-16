package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for OIDC provider configuration with full details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCProviderConfigDetailResponse {
    
    private UUID providerId;
    private String providerName;
    private String displayName;
    private String providerType;
    private Boolean enabled;
    
    // Full configuration details (excluding sensitive data like clientSecret)
    private OIDCConfigurationResponse configuration;
    private AttributeMappingConfigResponse attributeMapping;
    private RoleMappingConfigResponse roleMapping;
    private JITProvisioningConfigResponse jitProvisioning;
    
    private Map<String, Object> metadata;
    private Long version;
    private Instant createdAt;
    private Instant lastModifiedAt;
}
