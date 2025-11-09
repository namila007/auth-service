package me.namila.service.auth.data.governance.projection;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data projection for AuditLog summary (minimal data).
 */
public interface AuditLogSummaryProjection {
    
    UUID getAuditId();
    
    Instant getTimestamp();
    
    String getEventType();
    
    UUID getActorId();
    
    String getActorType();
    
    UUID getSubjectId();
    
    String getResource();
    
    String getAction();
    
    String getDecision();
    
    String getCorrelationId();
}

