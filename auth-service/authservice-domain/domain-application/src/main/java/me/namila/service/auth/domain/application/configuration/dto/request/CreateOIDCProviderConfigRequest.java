package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for creating a new OIDC provider configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOIDCProviderConfigRequest {
    
    @NotBlank(message = "Provider name is required")
    private String providerName;
    
    @NotBlank(message = "Display name is required")
    private String displayName;
    
    @NotNull(message = "Provider type is required")
    private String providerType; // OIDC, SAML
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Valid
    @NotNull(message = "OIDC configuration is required")
    private OIDCConfigurationRequest configuration;
    
    @Valid
    private AttributeMappingConfigRequest attributeMapping;
    
    @Valid
    private RoleMappingConfigRequest roleMapping;
    
    @Valid
    private JITProvisioningConfigRequest jitProvisioning;
    
    private Map<String, Object> metadata;
}
