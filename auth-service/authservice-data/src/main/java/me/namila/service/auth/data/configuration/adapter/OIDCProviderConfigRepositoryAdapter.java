package me.namila.service.auth.data.configuration.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.configuration.mapper.OIDCProviderConfigEntityMapper;
import me.namila.service.auth.data.configuration.repository.OIDCProviderConfigJpaRepository;
import me.namila.service.auth.domain.application.port.configuration.OIDCProviderConfigRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter for OIDCProviderConfig aggregate root.
 * Implements OIDCProviderConfigRepositoryPort using JPA.
 */
@Component
@RequiredArgsConstructor
public class OIDCProviderConfigRepositoryAdapter implements OIDCProviderConfigRepositoryPort {
    
    private final OIDCProviderConfigJpaRepository jpaRepository;
    private final OIDCProviderConfigEntityMapper mapper;
    
    @Override
    public OIDCProviderConfig save(OIDCProviderConfig config) {
        var entity = mapper.toEntity(config);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<OIDCProviderConfig> findById(UUID providerId) {
        return jpaRepository.findById(providerId)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<OIDCProviderConfig> findByProviderName(String providerName) {
        return jpaRepository.findByProviderName(providerName)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByProviderName(String providerName) {
        return jpaRepository.existsByProviderName(providerName);
    }
    
    @Override
    public List<OIDCProviderConfig> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<OIDCProviderConfig> findEnabledConfigs() {
        return jpaRepository.findByEnabled(true).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID providerId) {
        jpaRepository.deleteById(providerId);
    }
}

