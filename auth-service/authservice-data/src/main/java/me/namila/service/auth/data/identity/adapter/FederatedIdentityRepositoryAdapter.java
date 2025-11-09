package me.namila.service.auth.data.identity.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.identity.entity.FederatedIdentityEntity;
import me.namila.service.auth.data.identity.mapper.FederatedIdentityEntityMapper;
import me.namila.service.auth.data.identity.repository.FederatedIdentityJpaRepository;
import me.namila.service.auth.domain.application.port.identity.FederatedIdentityRepositoryPort;
import me.namila.service.auth.domain.core.identity.model.FederatedIdentity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public FederatedIdentity save(FederatedIdentity federatedIdentity) {
        var entity = mapper.toEntity(federatedIdentity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<FederatedIdentity> findById(UUID federatedIdentityId) {
        return jpaRepository.findById(federatedIdentityId)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<FederatedIdentity> findByUserId(UUID userId) {
        return jpaRepository.findByUser_UserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<FederatedIdentity> findByProviderAndSubject(UUID providerId, String subjectId) {
        return jpaRepository.findByProviderIdAndSubjectId(providerId, subjectId)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByProviderAndSubject(UUID providerId, String subjectId) {
        return jpaRepository.existsByProviderIdAndSubjectId(providerId, subjectId);
    }
    
    @Override
    public void deleteById(UUID federatedIdentityId) {
        jpaRepository.deleteById(federatedIdentityId);
    }
    
    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUser_UserId(userId);
    }
}

