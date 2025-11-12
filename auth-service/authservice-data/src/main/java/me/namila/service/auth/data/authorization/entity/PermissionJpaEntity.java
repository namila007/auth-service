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
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for Permission.
 */
@Entity
@Table(name = "permissions", schema = "authorization",
       uniqueConstraints = @UniqueConstraint(columnNames = {"resource", "action", "scope"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionJpaEntity
{
    
    @Id
    @Column(name = "permission_id")
    private UUID permissionId;
    
    @Column(name = "resource", nullable = false, length = 255)
    private String resource;
    
    @Column(name = "action", nullable = false, length = 100)
    private String action;
    
    @Column(name = "scope", length = 100)
    private String scope;
    
    @Column(name = "conditions", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> conditions;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

