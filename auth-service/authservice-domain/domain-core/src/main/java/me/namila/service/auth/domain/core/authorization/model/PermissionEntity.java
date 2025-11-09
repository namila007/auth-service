package me.namila.service.auth.domain.core.authorization.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseEntity;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;

import java.util.HashMap;
import java.util.Map;

/**
 * Permission entity representing a specific permission on a resource.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PermissionEntity extends BaseEntity<PermissionId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private PermissionId id = PermissionId.generate();
    
    private String resource;
    private String action;
    private String scope;
    
    @Builder.Default
    private Map<String, Object> conditions = new HashMap<>();
    
    private String description;
    
    public Map<String, Object> getConditions() {
        return conditions != null ? new HashMap<>(conditions) : new HashMap<>();
    }
    
    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions != null ? new HashMap<>(conditions) : new HashMap<>();
    }
}

