package me.namila.service.auth.data.authorization.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.authorization.mapper.RoleEntityMapper;
import me.namila.service.auth.data.authorization.repository.RoleJpaRepository;
import me.namila.service.auth.domain.application.port.authorization.RoleRepositoryPort;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter for Role aggregate root.
 */
@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    
    private final RoleJpaRepository jpaRepository;
    private final RoleEntityMapper mapper;
    
    @Override
    public RoleAggregate save(RoleAggregate role) {
        var entity = mapper.toEntity(role);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<RoleAggregate> findById(RoleId roleId) {
        return jpaRepository.findById(roleId.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<RoleAggregate> findByRoleName(String roleName) {
        return jpaRepository.findByRoleName(roleName)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByRoleName(String roleName) {
        return jpaRepository.existsByRoleName(roleName);
    }
    
    @Override
    public List<RoleAggregate> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<RoleAggregate> findByRoleType(RoleType roleType) {
        return jpaRepository.findByRoleType(roleType.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(RoleId roleId) {
        jpaRepository.deleteById(roleId.getValue());
    }
}

