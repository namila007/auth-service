package me.namila.service.auth.data.configuration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for OIDCProviderConfig aggregate root.
 */
@Entity
@Table(name = "oidc_provider_configs", schema = "configuration")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCProviderConfigJpaEntity
{
    
    @Id
    @Column(name = "provider_id")
    private UUID providerId;
    
    @Column(name = "provider_name", nullable = false, unique = true, length = 255)
    private String providerName;
    
    @Column(name = "provider_type", nullable = false, length = 50)
    private String providerType;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;
    
    @Column(name = "configuration", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> configuration;
    
    @Column(name = "attribute_mapping", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> attributeMapping;
    
    @Column(name = "role_mapping", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> roleMapping;
    
    @Column(name = "jit_provisioning", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> jitProvisioning;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

