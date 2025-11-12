package me.namila.service.auth.data.identity.entity;

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
 * JPA entity for FederatedIdentity.
 */
@Entity
@Table(name = "federated_identities", schema = "identity",
       uniqueConstraints = @UniqueConstraint(columnNames = {"provider_id", "subject_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FederatedIdentityJpaEntity
{
    
    @Id
    @Column(name = "federated_identity_id")
    private UUID federatedIdentityId;
    
    // FK column - this is what gets persisted
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    // Lazy relationship - for queries only, never updated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;
    
    @Column(name = "provider_id", nullable = false)
    private UUID providerId;
    
    @Column(name = "subject_id", nullable = false, length = 255)
    private String subjectId;
    
    @Column(name = "issuer", nullable = false, length = 500)
    private String issuer;
    
    @Column(name = "linked_at", nullable = false)
    private Instant linkedAt;
    
    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
}

