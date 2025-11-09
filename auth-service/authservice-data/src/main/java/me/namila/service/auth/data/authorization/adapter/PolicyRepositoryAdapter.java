package me.namila.service.auth.data.authorization.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.authorization.mapper.PolicyEntityMapper;
import me.namila.service.auth.data.authorization.repository.PolicyJpaRepository;
import me.namila.service.auth.domain.application.port.authorization.PolicyRepositoryPort;
import me.namila.service.auth.domain.core.authorization.model.Policy;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter for Policy aggregate root.
 */
@Component
@RequiredArgsConstructor
public class PolicyRepositoryAdapter implements PolicyRepositoryPort {
    
    private final PolicyJpaRepository jpaRepository;
    private final PolicyEntityMapper mapper;
    
    @Override
    public Policy save(Policy policy) {
        var entity = mapper.toEntity(policy);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Policy> findById(UUID policyId) {
        return jpaRepository.findById(policyId)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Policy> findByPolicyName(String policyName) {
        return jpaRepository.findByPolicyName(policyName)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Policy> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Policy> findEnabledPolicies() {
        return jpaRepository.findByEnabledTrue().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Policy> findByPolicyType(PolicyType policyType) {
        return jpaRepository.findByPolicyType(policyType.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Policy> findEnabledByPolicyType(PolicyType policyType) {
        return jpaRepository.findByPolicyTypeAndEnabledTrue(policyType.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID policyId) {
        jpaRepository.deleteById(policyId);
    }
}

