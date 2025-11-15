package me.namila.service.auth.data.authorization.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.data.identity.entity.UserJpaEntity;

/**
 * JPA entity for UserRoleAssignment aggregate root.
 */
@Entity
@Table(name = "user_role_assignments", schema = "authorizations")
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

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id",
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(name = "FK_user_role_assignment_user"))
  private UserJpaEntity user;

  @Column(name = "role_id", nullable = false)
  private UUID roleId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id",
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(name = "FK_user_role_assignment_role"))
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
