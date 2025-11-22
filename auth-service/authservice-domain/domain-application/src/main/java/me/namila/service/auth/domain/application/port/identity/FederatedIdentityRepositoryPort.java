package me.namila.service.auth.domain.application.port.identity;

import java.util.Optional;

import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;

public interface FederatedIdentityRepositoryPort {

    FederatedIdentityEntity save(FederatedIdentityEntity federatedIdentity);

    Optional<FederatedIdentityEntity> findById(FederatedIdentityId id);

    Optional<FederatedIdentityEntity> findByProviderIdAndSubjectId(OIDCProviderConfigId providerId, String subjectId);

    void delete(FederatedIdentityId id);
}
