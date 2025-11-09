package me.namila.service.auth.domain.application.port.authorization;

import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Role aggregate root.
 */
public interface RoleRepositoryPort {
    
    /**
     * Save or update a role.
     * @param role The role to save
     * @return The saved role
     */
    RoleAggregate save(RoleAggregate role);
    
    /**
     * Find a role by ID.
     * @param roleId The role ID
     * @return Optional containing the role if found
     */
    Optional<RoleAggregate> findById(RoleId roleId);
    
    /**
     * Find a role by role name.
     * @param roleName The role name
     * @return Optional containing the role if found
     */
    Optional<RoleAggregate> findByRoleName(String roleName);
    
    /**
     * Check if a role name exists.
     * @param roleName The role name
     * @return true if role name exists
     */
    boolean existsByRoleName(String roleName);
    
    /**
     * Find all roles.
     * @return List of all roles
     */
    List<RoleAggregate> findAll();
    
    /**
     * Find roles by type.
     * @param roleType The role type
     * @return List of roles with the given type
     */
    List<RoleAggregate> findByRoleType(me.namila.service.auth.domain.core.authorization.valueobject.RoleType roleType);
    
    /**
     * Delete a role by ID.
     * @param roleId The role ID
     */
    void deleteById(RoleId roleId);
}

