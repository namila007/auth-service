package me.namila.service.auth.domain.core.authorization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * UserRoleAssignment aggregate root representing a role assignment to a user.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "assignmentId")
public class UserRoleAssignment {
    
    @Builder.Default
    private UUID assignmentId = UUID.randomUUID();
    
    private UUID userId;
    private UUID roleId;
    private AssignmentScope scope;
    private String scopeContext; // e.g., tenantId, resourceId
    
    @Builder.Default
    private Instant effectiveFrom = Instant.now();
    
    private Instant effectiveUntil; // nullable
    private UUID assignedBy; // admin userId
    
    @Builder.Default
    private Instant assignedAt = Instant.now();
    
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ACTIVE;
    
    @Builder.Default
    private Long version = 0L;
    
    public void revoke(UUID revokedBy) {
        if (this.status == AssignmentStatus.REVOKED) {
            throw new IllegalStateException("Assignment is already revoked");
        }
        this.status = AssignmentStatus.REVOKED;
        this.assignedBy = revokedBy;
        this.assignedAt = Instant.now();
        incrementVersion();
    }
    
    public void expire() {
        if (this.status == AssignmentStatus.EXPIRED) {
            throw new IllegalStateException("Assignment is already expired");
        }
        this.status = AssignmentStatus.EXPIRED;
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

