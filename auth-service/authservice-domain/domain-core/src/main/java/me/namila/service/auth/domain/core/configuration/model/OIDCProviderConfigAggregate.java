package me.namila.service.auth.domain.core.configuration.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseAggregate;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;

import java.util.HashMap;
import java.util.Map;

/**
 * OIDCProviderConfig aggregate root representing an OIDC provider configuration.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OIDCProviderConfigAggregate extends BaseAggregate<OIDCProviderConfigId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private OIDCProviderConfigId id = OIDCProviderConfigId.generate();
    
    private String providerName; // Unique identifier (e.g., "azure-ad", "okta-corp")
    private ProviderType providerType;
    
    @Builder.Default
    private Boolean enabled = true;
    
    private String displayName;
    private OIDCConfiguration configuration;
    private AttributeMappingConfig attributeMapping;
    private RoleMappingConfig roleMapping;
    private JITProvisioningConfig jitProvisioning;
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    @Builder.Default
    private Long version = 0L;
    
    public void enable() {
        this.enabled = true;
        markAsUpdated();
        incrementVersion();
    }
    
    public void disable() {
        this.enabled = false;
        markAsUpdated();
        incrementVersion();
    }
    
    public void incrementVersion() {
        this.version = (this.version == null ? 0L : this.version) + 1L;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
}

