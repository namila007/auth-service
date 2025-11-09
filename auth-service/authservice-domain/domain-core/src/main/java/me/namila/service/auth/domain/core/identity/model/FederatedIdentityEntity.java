package me.namila.service.auth.domain.core.identity.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Federated identity entity linking external identity provider accounts to internal users.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class FederatedIdentityEntity extends BaseEntity<FederatedIdentityId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private FederatedIdentityId id = FederatedIdentityId.generate();
    
    private UserId userId;
    private UUID providerId;
    private String subjectId; // Immutable external IdP subject identifier
    private String issuer;
    
    @Builder.Default
    private Instant linkedAt = Instant.now();
    
    private Instant lastSyncedAt;
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    public void linkToUser(UserId userId) {
        if (this.userId != null && !this.userId.equals(userId)) {
            throw new IllegalStateException("Federated identity is already linked to a different user");
        }
        this.userId = userId;
        markAsUpdated();
    }
    
    public void updateSyncTimestamp() {
        this.lastSyncedAt = Instant.now();
        markAsUpdated();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata != null ? metadata : new HashMap<>();
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}

