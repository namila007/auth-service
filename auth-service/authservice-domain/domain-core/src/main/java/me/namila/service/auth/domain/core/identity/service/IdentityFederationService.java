package me.namila.service.auth.domain.core.identity.service;

import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;

/**
 * Domain service for identity federation operations.
 * Encapsulates logic for linking external identities to internal users.
 */
public class IdentityFederationService {

    public FederatedIdentityEntity createFederatedIdentity(UserAggregate user, OIDCProviderConfigId providerId,
            String subjectId, String issuer) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        FederatedIdentityId id = FederatedIdentityId.generate();

        return FederatedIdentityEntity.builder()
                .id(id)
                .userId(user.getId())
                .providerId(providerId)
                .subjectId(subjectId)
                .issuer(issuer)
                .linkedAt(java.time.LocalDateTime.now())
                .lastSyncedAt(java.time.LocalDateTime.now())
                .build();
    }

    public void linkIdentityToUser(UserAggregate user, FederatedIdentityEntity identity) {
        user.addFederatedIdentity(identity);
    }
}
