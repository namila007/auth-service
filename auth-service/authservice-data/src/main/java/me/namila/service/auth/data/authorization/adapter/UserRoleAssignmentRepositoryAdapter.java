package me.namila.service.auth.data.authorization.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.authorization.mapper.UserRoleAssignmentEntityMapper;
import me.namila.service.auth.data.authorization.repository.UserRoleAssignmentJpaRepository;
import me.namila.service.auth.domain.application.port.authorization.UserRoleAssignmentRepositoryPort;
import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignmentAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.model.id.UserRoleAssignmentId;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    public Optional<UserRoleAssignmentAggregate> findById(UserRoleAssignmentId assignmentId) {
        return jpaRepository.findById(assignmentId.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByUserId(UserId userId) {
        return jpaRepository.findByUser_UserId(userId.getValue()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findActiveByUserId(UserId userId) {
        Instant now = Instant.now();
        return jpaRepository.findByUser_UserId(userId.getValue()).stream()
            .map(mapper::toDomain)
            .filter(assignment -> assignment.isActive())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByRoleId(RoleId roleId) {
        return jpaRepository.findByRole_RoleId(roleId.getValue()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByUserIdAndRoleId(UserId userId, RoleId roleId) {
        return jpaRepository.findByUser_UserIdAndRole_RoleId(userId.getValue(), roleId.getValue()).stream()
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
    public void deleteById(UserRoleAssignmentId assignmentId) {
        jpaRepository.deleteById(assignmentId.getValue());
    }
    
    @Override
    public void deleteByUserId(UserId userId) {
        jpaRepository.deleteByUser_UserId(userId.getValue());
    }
    
    @Override
    public void deleteByRoleId(RoleId roleId) {
        jpaRepository.deleteByRole_RoleId(roleId.getValue());
    }
}

