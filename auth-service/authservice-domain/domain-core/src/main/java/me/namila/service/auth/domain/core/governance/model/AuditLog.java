package me.namila.service.auth.domain.core.governance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "auditId")
public class AuditLog {
    
    @Builder.Default
    private UUID auditId = UUID.randomUUID();
    
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

