package me.namila.service.auth.data.identity.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "federated_identities", schema = "identities", uniqueConstraints = {
        @UniqueConstraint(name = "UK_federated_identity_provider_subject", columnNames = { "provider_id",
                "subject_id" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FederatedIdentityJpaEntity {

    @Id
    @Column(name = "federated_identity_id")
    private UUID federatedIdentityId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_federated_identity_user"))
    private UserJpaEntity user;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "linked_at", nullable = false)
    private Instant linkedAt;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "metadata", columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private java.util.Map<String, Object> metadata;
}
