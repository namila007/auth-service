package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OIDCProviderConfig aggregate root representing an OIDC provider configuration.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "providerId")
public class OIDCProviderConfig {
    
    @Builder.Default
    private UUID providerId = UUID.randomUUID();
    
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
    private Instant createdAt = Instant.now();
    
    @Builder.Default
    private Instant lastModifiedAt = Instant.now();
    
    @Builder.Default
    private Long version = 0L;
    
    public void enable() {
        this.enabled = true;
        this.lastModifiedAt = Instant.now();
        incrementVersion();
    }
    
    public void disable() {
        this.enabled = false;
        this.lastModifiedAt = Instant.now();
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

