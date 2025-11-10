package me.namila.service.auth.data.authorization.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.authorization.mapper.UserRoleAssignmentEntityMapper;
import me.namila.service.auth.data.authorization.repository.UserRoleAssignmentJpaRepository;
import me.namila.service.auth.domain.application.port.authorization.UserRoleAssignmentRepositoryPort;
import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignmentAggregate;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter for UserRoleAssignment aggregate root.
 */
@Component
@RequiredArgsConstructor
public class UserRoleAssignmentRepositoryAdapter implements UserRoleAssignmentRepositoryPort {
    
    private final UserRoleAssignmentJpaRepository jpaRepository;
    private final UserRoleAssignmentEntityMapper mapper;
    
    @Override
    public UserRoleAssignmentAggregate save(UserRoleAssignmentAggregate assignment) {
        var entity = mapper.toEntity(assignment);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<UserRoleAssignmentAggregate> findById(UUID assignmentId) {
        return jpaRepository.findById(assignmentId)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByUserId(UUID userId) {
        return jpaRepository.findByUser_UserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findActiveByUserId(UUID userId) {
        Instant now = Instant.now();
        return jpaRepository.findByUser_UserId(userId).stream()
            .map(mapper::toDomain)
            .filter(assignment -> assignment.isActive())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByRoleId(UUID roleId) {
        return jpaRepository.findByRole_RoleId(roleId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByUserIdAndRoleId(UUID userId, UUID roleId) {
        return jpaRepository.findByUser_UserIdAndRole_RoleId(userId, roleId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByStatus(AssignmentStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByScope(AssignmentScope scope) {
        return jpaRepository.findByScope(scope.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID assignmentId) {
        jpaRepository.deleteById(assignmentId);
    }
    
    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUser_UserId(userId);
    }
    
    @Override
    public void deleteByRoleId(UUID roleId) {
        jpaRepository.deleteByRole_RoleId(roleId);
    }
}

