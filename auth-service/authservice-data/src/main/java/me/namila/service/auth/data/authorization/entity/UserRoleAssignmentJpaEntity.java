package me.namila.service.auth.data.authorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.data.identity.entity.UserJpaEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for UserRoleAssignment aggregate root.
 */
@Entity
@Table(name = "user_role_assignments", schema = "authorization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignmentJpaEntity
{
    
    @Id
    @Column(name = "assignment_id")
    private UUID assignmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleJpaEntity role;
    
    @Column(name = "scope", nullable = false, length = 50)
    private String scope;
    
    @Column(name = "scope_context", length = 255)
    private String scopeContext;
    
    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;
    
    @Column(name = "effective_until")
    private Instant effectiveUntil;
    
    @Column(name = "assigned_by", nullable = false)
    private UUID assignedBy;
    
    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

