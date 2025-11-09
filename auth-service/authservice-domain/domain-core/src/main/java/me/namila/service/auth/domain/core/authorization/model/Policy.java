package me.namila.service.auth.domain.core.authorization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.domain.core.authorization.valueobject.Effect;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Policy aggregate root representing an authorization policy (ABAC/PBAC).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "policyId")
public class Policy {
    
    @Builder.Default
    private UUID policyId = UUID.randomUUID();
    
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
    
    @Builder.Default
    private Instant createdAt = Instant.now();
    
    @Builder.Default
    private Instant lastModifiedAt = Instant.now();
    
    public void enable() {
        this.enabled = true;
        this.lastModifiedAt = Instant.now();
    }
    
    public void disable() {
        this.enabled = false;
        this.lastModifiedAt = Instant.now();
    }
    
    public void incrementVersion() {
        this.version = (this.version == null ? 1 : this.version) + 1;
        this.lastModifiedAt = Instant.now();
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

