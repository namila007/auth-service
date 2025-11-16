package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating an existing OIDC provider configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOIDCProviderConfigRequest {
    
    private String displayName;
    private Boolean enabled;
    
    @Valid
    private OIDCConfigurationRequest configuration;
    
    @Valid
    private AttributeMappingConfigRequest attributeMapping;
    
    @Valid
    private RoleMappingConfigRequest roleMapping;
    
    @Valid
    private JITProvisioningConfigRequest jitProvisioning;
    
    private Map<String, Object> metadata;
}
