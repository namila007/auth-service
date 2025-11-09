package me.namila.service.auth.domain.core.authorization.service;

import me.namila.service.auth.domain.core.authorization.model.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Domain service for permission aggregation.
 * Computes effective permissions from multiple roles.
 */
public class PermissionAggregationService {
    
    private final RoleHierarchyService roleHierarchyService;
    
    public PermissionAggregationService(RoleHierarchyService roleHierarchyService) {
        this.roleHierarchyService = roleHierarchyService;
    }
    
    /**
     * Aggregates effective permissions from multiple roles.
     * @param roles List of roles
     * @return Set of all effective permissions
     */
    public Set<PermissionEntity> aggregatePermissions(List<RoleAggregate> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<PermissionEntity> aggregatedPermissions = new HashSet<>();
        
        for (RoleAggregate role : roles) {
            if (role != null) {
                // Get effective permissions for the role (direct + inherited)
                Set<PermissionEntity> rolePermissions = roleHierarchyService.getEffectivePermissions(role);
                aggregatedPermissions.addAll(rolePermissions);
            }
        }
        
        return aggregatedPermissions;
    }
    
    /**
     * Checks if a user has a specific permission through any of their roles.
     * @param roles List of roles
     * @param requiredPermission The permission to check
     * @return true if user has the permission
     */
    public boolean hasPermission(List<RoleAggregate> roles, PermissionEntity requiredPermission) {
        if (roles == null || requiredPermission == null) {
            return false;
        }
        
        Set<PermissionEntity> effectivePermissions = aggregatePermissions(roles);
        
        return effectivePermissions.stream()
            .anyMatch(permission -> permission.equals(requiredPermission));
    }
    
    /**
     * Gets permissions filtered by resource.
     * @param roles List of roles
     * @param resource The resource to filter by
     * @return Set of permissions for the resource
     */
    public Set<PermissionEntity> getPermissionsByResource(List<RoleAggregate> roles, String resource) {
        if (roles == null || resource == null) {
            return new HashSet<>();
        }
        
        Set<PermissionEntity> allPermissions = aggregatePermissions(roles);
        
        return allPermissions.stream()
            .filter(permission -> resource.equals(permission.getResource()))
            .collect(java.util.stream.Collectors.toSet());
    }
}

