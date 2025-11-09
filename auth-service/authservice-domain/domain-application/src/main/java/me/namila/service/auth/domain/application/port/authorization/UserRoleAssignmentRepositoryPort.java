package me.namila.service.auth.domain.application.port.authorization;

import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignment;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for UserRoleAssignment aggregate root.
 */
public interface UserRoleAssignmentRepositoryPort {
    
    /**
     * Save or update a user role assignment.
     * @param assignment The assignment to save
     * @return The saved assignment
     */
    UserRoleAssignment save(UserRoleAssignment assignment);
    
    /**
     * Find an assignment by ID.
     * @param assignmentId The assignment ID
     * @return Optional containing the assignment if found
     */
    Optional<UserRoleAssignment> findById(UUID assignmentId);
    
    /**
     * Find all assignments for a user.
     * @param userId The user ID
     * @return List of assignments for the user
     */
    List<UserRoleAssignment> findByUserId(UUID userId);
    
    /**
     * Find active assignments for a user.
     * @param userId The user ID
     * @return List of active assignments for the user
     */
    List<UserRoleAssignment> findActiveByUserId(UUID userId);
    
    /**
     * Find all assignments for a role.
     * @param roleId The role ID
     * @return List of assignments for the role
     */
    List<UserRoleAssignment> findByRoleId(UUID roleId);
    
    /**
     * Find assignments by user and role.
     * @param userId The user ID
     * @param roleId The role ID
     * @return List of assignments matching user and role
     */
    List<UserRoleAssignment> findByUserIdAndRoleId(UUID userId, UUID roleId);
    
    /**
     * Find assignments by status.
     * @param status The assignment status
     * @return List of assignments with the given status
     */
    List<UserRoleAssignment> findByStatus(AssignmentStatus status);
    
    /**
     * Find assignments by scope.
     * @param scope The assignment scope
     * @return List of assignments with the given scope
     */
    List<UserRoleAssignment> findByScope(AssignmentScope scope);
    
    /**
     * Delete an assignment by ID.
     * @param assignmentId The assignment ID
     */
    void deleteById(UUID assignmentId);
    
    /**
     * Delete all assignments for a user.
     * @param userId The user ID
     */
    void deleteByUserId(UUID userId);
    
    /**
     * Delete all assignments for a role.
     * @param roleId The role ID
     */
    void deleteByRoleId(UUID roleId);
}

