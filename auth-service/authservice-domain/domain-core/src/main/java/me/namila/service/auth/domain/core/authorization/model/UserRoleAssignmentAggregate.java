package me.namila.service.auth.domain.core.authorization.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.model.id.UserRoleAssignmentId;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;
import me.namila.service.auth.domain.core.identity.model.id.UserId;

import java.time.Instant;

/**
 * UserRoleAssignment aggregate root representing a role assignment to a user.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserRoleAssignmentAggregate extends BaseAggregate<UserRoleAssignmentId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private UserRoleAssignmentId id = UserRoleAssignmentId.generate();
    
    private UserId userId;
    private RoleId roleId;
    private AssignmentScope scope;
    private String scopeContext; // e.g., tenantId, resourceId
    
    @Builder.Default
    private Instant effectiveFrom = Instant.now();
    
    private Instant effectiveUntil; // nullable
    private UserId assignedBy; // admin userId
    
    @Builder.Default
    private Instant assignedAt = Instant.now();
    
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ACTIVE;
    
    @Builder.Default
    private Long version = 0L;
    
    public void revoke(UserId revokedBy) {
        if (this.status == AssignmentStatus.REVOKED) {
            throw new IllegalStateException("Assignment is already revoked");
        }
        this.status = AssignmentStatus.REVOKED;
        this.assignedBy = revokedBy;
        this.assignedAt = Instant.now();
        markAsUpdated();
        incrementVersion();
    }
    
    public void expire() {
        if (this.status == AssignmentStatus.EXPIRED) {
            throw new IllegalStateException("Assignment is already expired");
        }
        this.status = AssignmentStatus.EXPIRED;
        markAsUpdated();
        incrementVersion();
    }
    
    public boolean isActive() {
        if (status != AssignmentStatus.ACTIVE) {
            return false;
        }
        Instant now = Instant.now();
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) {
            return false;
        }
        if (effectiveUntil != null && now.isAfter(effectiveUntil)) {
            return false;
        }
        return true;
    }
    
    public void incrementVersion() {
        this.version = (this.version == null ? 0L : this.version) + 1L;
    }
}

