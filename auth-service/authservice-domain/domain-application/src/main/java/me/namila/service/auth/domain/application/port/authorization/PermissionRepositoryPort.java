package me.namila.service.auth.domain.application.port.authorization;

import me.namila.service.auth.domain.core.authorization.model.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Permission entity.
 */
public interface PermissionRepositoryPort {
    
    /**
     * Save or update a permission.
     * @param permission The permission to save
     * @return The saved permission
     */
    PermissionEntity save(PermissionEntity permission);
    
    /**
     * Find a permission by ID.
     * @param permissionId The permission ID
     * @return Optional containing the permission if found
     */
    Optional<PermissionEntity> findById(PermissionId permissionId);
    
    /**
     * Find all permissions.
     * @return List of all permissions
     */
    List<PermissionEntity> findAll();
    
    /**
     * Find permissions by resource.
     * @param resource The resource
     * @return List of permissions for the resource
     */
    List<PermissionEntity> findByResource(String resource);
    
    /**
     * Find permissions by resource and action.
     * @param resource The resource
     * @param action The action
     * @return List of permissions matching resource and action
     */
    List<PermissionEntity> findByResourceAndAction(String resource, String action);
    
    /**
     * Delete a permission by ID.
     * @param permissionId The permission ID
     */
    void deleteById(PermissionId permissionId);
}

