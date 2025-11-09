package me.namila.service.auth.domain.core.authorization.service;

import me.namila.service.auth.domain.core.authorization.model.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain service for role hierarchy operations.
 * Handles role hierarchy resolution and inherited permissions calculation.
 */
public class RoleHierarchyService {
    
    /**
     * Gets all inherited permissions from parent roles.
     * @param role The role to get inherited permissions for
     * @return Set of inherited permissions
     */
    public Set<PermissionEntity> getInheritedPermissions(RoleAggregate role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        Set<PermissionEntity> inheritedPermissions = new HashSet<>();
        
        // Recursively collect permissions from parent roles
        for (RoleAggregate parentRole : role.getParentRoles()) {
            // Add direct permissions from parent
            inheritedPermissions.addAll(parentRole.getPermissions());
            
            // Recursively get permissions from parent's parents
            inheritedPermissions.addAll(getInheritedPermissions(parentRole));
        }
        
        return inheritedPermissions;
    }
    
    /**
     * Gets all effective permissions for a role (direct + inherited).
     * @param role The role to get effective permissions for
     * @return Set of all effective permissions
     */
    public Set<PermissionEntity> getEffectivePermissions(RoleAggregate role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        Set<PermissionEntity> effectivePermissions = new HashSet<>();
        
        // Add direct permissions
        effectivePermissions.addAll(role.getPermissions());
        
        // Add inherited permissions
        effectivePermissions.addAll(getInheritedPermissions(role));
        
        return effectivePermissions;
    }
    
    /**
     * Validates role hierarchy to prevent circular dependencies.
     * @param role The role to validate
     * @param potentialParent The potential parent role
     * @throws IllegalArgumentException if adding parent would create a cycle
     */
    public void validateHierarchy(RoleAggregate role, RoleAggregate potentialParent) {
        if (role == null || potentialParent == null) {
            throw new IllegalArgumentException("Role and potential parent cannot be null");
        }
        
        if (role.equals(potentialParent)) {
            throw new IllegalArgumentException("Role cannot be its own parent");
        }
        
        // Check if potential parent is already a descendant of role (would create cycle)
        if (isDescendant(potentialParent, role)) {
            throw new IllegalArgumentException("Cannot add parent role: would create circular dependency");
        }
    }
    
    /**
     * Checks if a role is a descendant of another role.
     * @param role The role to check
     * @param ancestor The potential ancestor
     * @return true if role is a descendant of ancestor
     */
    private boolean isDescendant(RoleAggregate role, RoleAggregate ancestor) {
        for (RoleAggregate parent : role.getParentRoles()) {
            if (parent.equals(ancestor)) {
                return true;
            }
            if (isDescendant(parent, ancestor)) {
                return true;
            }
        }
        return false;
    }
}

