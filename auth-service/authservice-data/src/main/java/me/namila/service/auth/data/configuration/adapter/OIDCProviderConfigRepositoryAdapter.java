package me.namila.service.auth.data.configuration.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.configuration.mapper.OIDCProviderConfigEntityMapper;
import me.namila.service.auth.data.configuration.repository.OIDCProviderConfigJpaRepository;
import me.namila.service.auth.domain.application.port.configuration.OIDCProviderConfigRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
    public OIDCProviderConfigAggregate save(OIDCProviderConfigAggregate config) {
        var entity = mapper.toEntity(config);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<OIDCProviderConfigAggregate> findById(OIDCProviderConfigId providerId) {
        return jpaRepository.findById(providerId.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<OIDCProviderConfigAggregate> findByProviderName(String providerName) {
        return jpaRepository.findByProviderName(providerName)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByProviderName(String providerName) {
        return jpaRepository.existsByProviderName(providerName);
    }
    
    @Override
    public List<OIDCProviderConfigAggregate> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<OIDCProviderConfigAggregate> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<OIDCProviderConfigAggregate> findEnabledConfigs() {
        return jpaRepository.findByEnabled(true).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(OIDCProviderConfigId providerId) {
        jpaRepository.deleteById(providerId.getValue());
    }
}

