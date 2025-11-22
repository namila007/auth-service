package me.namila.service.auth.domain.core.identity.model.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseEntity;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;

/**
 * Represents a federated identity linked to a user.
 */
@Getter
@SuperBuilder
public class FederatedIdentityEntity extends BaseEntity<FederatedIdentityId> {

    private UserId userId;
    private OIDCProviderConfigId providerId;
    private String subjectId;
    private String issuer;
    private LocalDateTime linkedAt;
    private LocalDateTime lastSyncedAt;

    private java.util.Map<String, Object> metadata;

    public FederatedIdentityEntity(FederatedIdentityId id, UserId userId, OIDCProviderConfigId providerId,
            String subjectId, String issuer) {
        super(id);
        this.userId = userId;
        this.providerId = providerId;
        this.subjectId = subjectId;
        this.issuer = issuer;
        this.linkedAt = LocalDateTime.now();
        this.lastSyncedAt = LocalDateTime.now();
        this.metadata = new java.util.HashMap<>();
    }

    public void updateLastSyncedAt() {
        this.lastSyncedAt = LocalDateTime.now();
        markAsUpdated();
    }

    public void setMetadata(java.util.Map<String, Object> metadata) {
        this.metadata = metadata;
        markAsUpdated();
    }
}
