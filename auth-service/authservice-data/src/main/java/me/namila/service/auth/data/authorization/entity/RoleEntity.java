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
 * JPA entity for Role aggregate root.
 */
@Entity
@Table(name = "roles", schema = "authorization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    
    @Id
    @Column(name = "role_id")
    private UUID roleId;
    
    @Column(name = "role_name", nullable = false, unique = true, length = 255)
    private String roleName;
    
    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "role_type", nullable = false, length = 50)
    private String roleType;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

