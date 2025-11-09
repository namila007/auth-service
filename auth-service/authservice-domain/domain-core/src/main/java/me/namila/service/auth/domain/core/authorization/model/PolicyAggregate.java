package me.namila.service.auth.domain.core.authorization.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.PolicyId;
import me.namila.service.auth.domain.core.authorization.valueobject.Effect;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Policy aggregate root representing an authorization policy (ABAC/PBAC).
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PolicyAggregate extends BaseAggregate<PolicyId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private PolicyId id = PolicyId.generate();
    
    private String policyName;
    private PolicyType policyType;
    
    @Builder.Default
    private Effect effect = Effect.DENY; // Default deny
    
    @Builder.Default
    private Map<String, Object> subjects = new HashMap<>(); // Subject matcher (user, role, group conditions)
    
    @Builder.Default
    private Map<String, Object> resources = new HashMap<>(); // Resource matcher (resource type, attributes)
    
    @Builder.Default
    private Set<String> actions = new HashSet<>();
    
    @Builder.Default
    private Map<String, Object> conditions = new HashMap<>(); // Attribute expressions
    
    @Builder.Default
    private Integer priority = 0;
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Builder.Default
    private Integer version = 1;
    
    public void enable() {
        this.enabled = true;
        markAsUpdated();
    }
    
    public void disable() {
        this.enabled = false;
        markAsUpdated();
    }
    
    public void incrementVersion() {
        this.version = (this.version == null ? 1 : this.version) + 1;
        markAsUpdated();
    }
    
    public Map<String, Object> getSubjects() {
        return subjects != null ? new HashMap<>(subjects) : new HashMap<>();
    }
    
    public void setSubjects(Map<String, Object> subjects) {
        this.subjects = subjects != null ? new HashMap<>(subjects) : new HashMap<>();
    }
    
    public Map<String, Object> getResources() {
        return resources != null ? new HashMap<>(resources) : new HashMap<>();
    }
    
    public void setResources(Map<String, Object> resources) {
        this.resources = resources != null ? new HashMap<>(resources) : new HashMap<>();
    }
    
    public Set<String> getActions() {
        return actions != null ? new HashSet<>(actions) : new HashSet<>();
    }
    
    public void setActions(Set<String> actions) {
        this.actions = actions != null ? new HashSet<>(actions) : new HashSet<>();
    }
    
    public Map<String, Object> getConditions() {
        return conditions != null ? new HashMap<>(conditions) : new HashMap<>();
    }
    
    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions != null ? new HashMap<>(conditions) : new HashMap<>();
    }
}

