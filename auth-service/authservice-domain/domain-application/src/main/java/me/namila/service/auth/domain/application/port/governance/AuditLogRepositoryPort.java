package me.namila.service.auth.domain.application.port.governance;

import me.namila.service.auth.domain.core.governance.model.AuditLogEntity;
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
    AuditLogEntity save(AuditLogEntity auditLog);
    
    /**
     * Find an audit log by ID.
     * @param auditId The audit ID
     * @return Optional containing the audit log if found
     */
    Optional<AuditLogEntity> findById(UUID auditId);
    
    /**
     * Find audit logs by actor ID.
     * @param actorId The actor ID
     * @return List of audit logs for the actor
     */
    List<AuditLogEntity> findByActorId(UUID actorId);
    
    /**
     * Find audit logs by subject ID.
     * @param subjectId The subject ID
     * @return List of audit logs for the subject
     */
    List<AuditLogEntity> findBySubjectId(UUID subjectId);
    
    /**
     * Find audit logs by event type.
     * @param eventType The event type
     * @return List of audit logs with the given event type
     */
    List<AuditLogEntity> findByEventType(AuditEventType eventType);
    
    /**
     * Find audit logs by correlation ID.
     * @param correlationId The correlation ID
     * @return List of audit logs with the given correlation ID
     */
    List<AuditLogEntity> findByCorrelationId(String correlationId);
    
    /**
     * Find audit logs within a time range.
     * @param startTime The start time
     * @param endTime The end time
     * @return List of audit logs within the time range
     */
    List<AuditLogEntity> findByTimestampBetween(Instant startTime, Instant endTime);
    
    /**
     * Find audit logs by resource.
     * @param resource The resource
     * @return List of audit logs for the resource
     */
    List<AuditLogEntity> findByResource(String resource);
}

