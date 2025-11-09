package me.namila.service.auth.data.authorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for Policy aggregate root.
 */
@Entity
@Table(name = "policies", schema = "authorization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEntity {
    
    @Id
    @Column(name = "policy_id")
    private UUID policyId;
    
    @Column(name = "policy_name", nullable = false, unique = true, length = 255)
    private String policyName;
    
    @Column(name = "policy_type", nullable = false, length = 50)
    private String policyType;
    
    @Column(name = "effect", nullable = false, length = 20)
    private String effect;
    
    @Column(name = "subjects", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> subjects;
    
    @Column(name = "resources", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> resources;
    
    @Column(name = "actions", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> actions;
    
    @Column(name = "conditions", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> conditions;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;
}

