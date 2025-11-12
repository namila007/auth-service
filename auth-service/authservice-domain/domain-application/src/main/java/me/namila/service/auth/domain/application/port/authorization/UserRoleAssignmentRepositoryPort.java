package me.namila.service.auth.domain.application.port.authorization;

import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignmentAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.model.id.UserRoleAssignmentId;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;
import me.namila.service.auth.domain.core.identity.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for UserRoleAssignment aggregate root.
 */
public interface UserRoleAssignmentRepositoryPort {
    
    /**
     * Save or update a user role assignment.
     * @param assignment The assignment to save
     * @return The saved assignment
     */
    UserRoleAssignmentAggregate save(UserRoleAssignmentAggregate assignment);
    
    /**
     * Find an assignment by ID.
     * @param assignmentId The assignment ID
     * @return Optional containing the assignment if found
     */
    Optional<UserRoleAssignmentAggregate> findById(UserRoleAssignmentId assignmentId);
    
    /**
     * Find all assignments for a user.
     * @param userId The user ID
     * @return List of assignments for the user
     */
    List<UserRoleAssignmentAggregate> findByUserId(UserId userId);
    
    /**
     * Find active assignments for a user.
     * @param userId The user ID
     * @return List of active assignments for the user
     */
    List<UserRoleAssignmentAggregate> findActiveByUserId(UserId userId);
    
    /**
     * Find all assignments for a role.
     * @param roleId The role ID
     * @return List of assignments for the role
     */
    List<UserRoleAssignmentAggregate> findByRoleId(RoleId roleId);
    
    /**
     * Find assignments by user and role.
     * @param userId The user ID
     * @param roleId The role ID
     * @return List of assignments matching user and role
     */
    List<UserRoleAssignmentAggregate> findByUserIdAndRoleId(UserId userId, RoleId roleId);
    
    /**
     * Find assignments by status.
     * @param status The assignment status
     * @return List of assignments with the given status
     */
    List<UserRoleAssignmentAggregate> findByStatus(AssignmentStatus status);
    
    /**
     * Find assignments by scope.
     * @param scope The assignment scope
     * @return List of assignments with the given scope
     */
    List<UserRoleAssignmentAggregate> findByScope(AssignmentScope scope);
    
    /**
     * Delete an assignment by ID.
     * @param assignmentId The assignment ID
     */
    void deleteById(UserRoleAssignmentId assignmentId);
    
    /**
     * Delete all assignments for a user.
     * @param userId The user ID
     */
    void deleteByUserId(UserId userId);
    
    /**
     * Delete all assignments for a role.
     * @param roleId The role ID
     */
    void deleteByRoleId(RoleId roleId);
}

