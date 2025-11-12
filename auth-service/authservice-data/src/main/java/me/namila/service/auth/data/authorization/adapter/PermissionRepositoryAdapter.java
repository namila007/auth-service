package me.namila.service.auth.data.authorization.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.authorization.mapper.PermissionEntityMapper;
import me.namila.service.auth.data.authorization.repository.PermissionJpaRepository;
import me.namila.service.auth.domain.application.port.authorization.PermissionRepositoryPort;
import me.namila.service.auth.domain.core.authorization.model.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository adapter for Permission entity.
 */
@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {
    
    private final PermissionJpaRepository jpaRepository;
    private final PermissionEntityMapper mapper;
    
    @Override
    public PermissionEntity save(PermissionEntity permission) {
        var entity = mapper.toEntity(permission);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<PermissionEntity> findById(PermissionId permissionId) {
        return jpaRepository.findById(permissionId.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<PermissionEntity> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PermissionEntity> findByResource(String resource) {
        return jpaRepository.findByResource(resource).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PermissionEntity> findByResourceAndAction(String resource, String action) {
        return jpaRepository.findByResourceAndAction(resource, action).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(PermissionId permissionId) {
        jpaRepository.deleteById(permissionId.getValue());
    }
}

