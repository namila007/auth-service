package me.namila.service.auth.domain.application.port.governance;

import me.namila.service.auth.domain.core.governance.model.AuditLog;
import me.namila.service.auth.domain.core.governance.valueobject.AuditEventType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for AuditLog entity.
 */
public interface AuditLogRepositoryPort {
    
    /**
     * Save an audit log entry.
     * @param auditLog The audit log to save
     * @return The saved audit log
     */
    AuditLog save(AuditLog auditLog);
    
    /**
     * Find an audit log by ID.
     * @param auditId The audit ID
     * @return Optional containing the audit log if found
     */
    Optional<AuditLog> findById(UUID auditId);
    
    /**
     * Find audit logs by actor ID.
     * @param actorId The actor ID
     * @return List of audit logs for the actor
     */
    List<AuditLog> findByActorId(UUID actorId);
    
    /**
     * Find audit logs by subject ID.
     * @param subjectId The subject ID
     * @return List of audit logs for the subject
     */
    List<AuditLog> findBySubjectId(UUID subjectId);
    
    /**
     * Find audit logs by event type.
     * @param eventType The event type
     * @return List of audit logs with the given event type
     */
    List<AuditLog> findByEventType(AuditEventType eventType);
    
    /**
     * Find audit logs by correlation ID.
     * @param correlationId The correlation ID
     * @return List of audit logs with the given correlation ID
     */
    List<AuditLog> findByCorrelationId(String correlationId);
    
    /**
     * Find audit logs within a time range.
     * @param startTime The start time
     * @param endTime The end time
     * @return List of audit logs within the time range
     */
    List<AuditLog> findByTimestampBetween(Instant startTime, Instant endTime);
    
    /**
     * Find audit logs by resource.
     * @param resource The resource
     * @return List of audit logs for the resource
     */
    List<AuditLog> findByResource(String resource);
}

