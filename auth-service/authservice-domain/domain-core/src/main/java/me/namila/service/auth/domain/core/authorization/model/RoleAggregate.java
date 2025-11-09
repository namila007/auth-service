package me.namila.service.auth.domain.core.authorization.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Role aggregate root representing a role with permissions.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RoleAggregate extends BaseAggregate<RoleId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private RoleId id = RoleId.generate();
    
    private String roleName; // Unique identifier (e.g., "platform:administrator")
    private String displayName;
    private String description;
    private RoleType roleType;
    
    @Builder.Default
    private Set<PermissionEntity> permissions = new HashSet<>();
    
    @Builder.Default
    private Set<RoleAggregate> parentRoles = new HashSet<>(); // For hierarchical RBAC
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    @Builder.Default
    private Long version = 0L;
    
    public void addPermission(PermissionEntity permission) {
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        this.permissions.add(permission);
        markAsUpdated();
        incrementVersion();
    }
    
    public void removePermission(PermissionEntity permission) {
        if (permission != null) {
            this.permissions.remove(permission);
            markAsUpdated();
            incrementVersion();
        }
    }
    
    public void addParentRole(RoleAggregate parentRole) {
        if (parentRole == null) {
            throw new IllegalArgumentException("Parent role cannot be null");
        }
        if (parentRole.equals(this)) {
            throw new IllegalArgumentException("Role cannot be its own parent");
        }
        if (this.parentRoles.contains(parentRole)) {
            throw new IllegalStateException("Role is already a parent");
        }
        this.parentRoles.add(parentRole);
        markAsUpdated();
        incrementVersion();
    }
    
    public void removeParentRole(RoleAggregate parentRole) {
        if (parentRole != null) {
            this.parentRoles.remove(parentRole);
            markAsUpdated();
            incrementVersion();
        }
    }
    
    public Set<PermissionEntity> getPermissions() {
        return permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }
    
    public void setPermissions(Set<PermissionEntity> permissions) {
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }
    
    public Set<RoleAggregate> getParentRoles() {
        return parentRoles != null ? new HashSet<>(parentRoles) : new HashSet<>();
    }
    
    public void setParentRoles(Set<RoleAggregate> parentRoles) {
        this.parentRoles = parentRoles != null ? new HashSet<>(parentRoles) : new HashSet<>();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    public void incrementVersion() {
        this.version = (this.version == null ? 0L : this.version) + 1L;
    }
}

