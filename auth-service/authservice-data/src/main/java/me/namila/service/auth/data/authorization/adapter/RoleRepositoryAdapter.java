package me.namila.service.auth.data.authorization.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.authorization.mapper.RoleEntityMapper;
import me.namila.service.auth.data.authorization.repository.RoleJpaRepository;
import me.namila.service.auth.domain.application.port.authorization.RoleRepositoryPort;
import me.namila.service.auth.domain.core.authorization.model.Role;
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
    public Role save(Role role) {
        var entity = mapper.toEntity(role);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Role> findById(UUID roleId) {
        return jpaRepository.findById(roleId)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return jpaRepository.findByRoleName(roleName)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByRoleName(String roleName) {
        return jpaRepository.existsByRoleName(roleName);
    }
    
    @Override
    public List<Role> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Role> findByRoleType(RoleType roleType) {
        return jpaRepository.findByRoleType(roleType.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID roleId) {
        jpaRepository.deleteById(roleId);
    }
}

