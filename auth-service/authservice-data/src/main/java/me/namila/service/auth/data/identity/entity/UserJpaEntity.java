package me.namila.service.auth.data.identity.entity;

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
 * JPA entity for User aggregate root.
 */
@Entity
@Table(name = "users", schema = "identity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity
{
    
    @Id
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
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

