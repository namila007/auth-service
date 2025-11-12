package me.namila.service.auth.data.identity.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.identity.mapper.FederatedIdentityEntityMapper;
import me.namila.service.auth.data.identity.repository.FederatedIdentityJpaRepository;
import me.namila.service.auth.domain.application.port.identity.FederatedIdentityRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository adapter for FederatedIdentity entity.
 */
@Component
@RequiredArgsConstructor
public class FederatedIdentityRepositoryAdapter implements FederatedIdentityRepositoryPort {

    private final FederatedIdentityJpaRepository jpaRepository;
    private final FederatedIdentityEntityMapper mapper;

    @Override
    public FederatedIdentityEntity save(FederatedIdentityEntity federatedIdentity) {
        var entity = mapper.toEntity(federatedIdentity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<FederatedIdentityEntity> findById(FederatedIdentityId federatedIdentityId) {
        return jpaRepository.findById(federatedIdentityId.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public List<FederatedIdentityEntity> findByUserId(UserId userId) {
        return jpaRepository.findByUser_UserId(userId.getValue()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<FederatedIdentityEntity> findByProviderAndSubject(OIDCProviderConfigId providerId, String subjectId) {
        return jpaRepository.findByProviderIdAndSubjectId(providerId.getValue(), subjectId)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByProviderAndSubject(OIDCProviderConfigId providerId, String subjectId) {
        return jpaRepository.existsByProviderIdAndSubjectId(providerId.getValue(), subjectId);
    }

    @Override
    public void deleteById(FederatedIdentityId federatedIdentityId) {
        jpaRepository.deleteById(federatedIdentityId.getValue());
    }
    
    @Override
    public void deleteByUserId(UserId userId) {
        jpaRepository.deleteByUser_UserId(userId.getValue());
    }
}

