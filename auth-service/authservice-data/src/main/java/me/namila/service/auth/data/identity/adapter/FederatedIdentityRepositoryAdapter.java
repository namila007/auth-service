package me.namila.service.auth.data.identity.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.identity.entity.FederatedIdentityJpaEntity;
import me.namila.service.auth.data.identity.mapper.FederatedIdentityMapper;
import me.namila.service.auth.data.identity.repository.FederatedIdentityJpaRepository;
import me.namila.service.auth.domain.application.port.identity.FederatedIdentityRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;

@Component
@RequiredArgsConstructor
public class FederatedIdentityRepositoryAdapter implements FederatedIdentityRepositoryPort {

    private final FederatedIdentityJpaRepository jpaRepository;
    private final FederatedIdentityMapper mapper;

    @Override
    public FederatedIdentityEntity save(FederatedIdentityEntity federatedIdentity) {
        FederatedIdentityJpaEntity entity = mapper.toEntity(federatedIdentity);
        FederatedIdentityJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<FederatedIdentityEntity> findById(FederatedIdentityId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<FederatedIdentityEntity> findByProviderIdAndSubjectId(OIDCProviderConfigId providerId,
            String subjectId) {
        return jpaRepository.findByProviderIdAndSubjectId(providerId.getValue(), subjectId)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(FederatedIdentityId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
