package me.namila.service.auth.data.governance.entity;

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
 * JPA entity for AuditLog entity.
 */
@Entity
@Table(name = "audit_logs", schema = "governance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogJpaEntity
{
    
    @Id
    @Column(name = "audit_id")
    private UUID auditId;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    
    @Column(name = "actor_id")
    private UUID actorId;
    
    @Column(name = "actor_type", nullable = false, length = 50)
    private String actorType;
    
    @Column(name = "subject_id")
    private UUID subjectId;
    
    @Column(name = "resource", length = 255)
    private String resource;
    
    @Column(name = "action", length = 100)
    private String action;
    
    @Column(name = "decision", length = 50)
    private String decision;
    
    @Column(name = "policy_version", length = 100)
    private String policyVersion;
    
    @Column(name = "context", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> context;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "correlation_id", length = 100)
    private String correlationId;
}

