package me.namila.service.auth.domain.core.governance.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseEntity;
import me.namila.service.auth.domain.core.governance.model.id.AuditLogId;
import me.namila.service.auth.domain.core.governance.valueobject.ActorType;
import me.namila.service.auth.domain.core.governance.valueobject.AuditEventType;
import me.namila.service.auth.domain.core.governance.valueobject.Decision;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AuditLog entity representing an immutable audit event.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AuditLogEntity extends BaseEntity<AuditLogId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private AuditLogId id = AuditLogId.generate();
    
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    private AuditEventType eventType;
    private UUID actorId; // Who performed the action
    private ActorType actorType;
    private UUID subjectId; // Target user/resource
    private String resource;
    private String action;
    private Decision decision;
    private String policyVersion;
    
    @Builder.Default
    private Map<String, Object> context = new HashMap<>();
    
    private String ipAddress;
    private String userAgent;
    private String correlationId; // For PDP/PEP correlation
    
    public Map<String, Object> getContext() {
        return context != null ? new HashMap<>(context) : new HashMap<>();
    }
    
    public void setContext(Map<String, Object> context) {
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }
}

