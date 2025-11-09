package me.namila.service.auth.data.governance.repository;

import me.namila.service.auth.data.governance.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for AuditLogEntity.
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, UUID> {
    
    List<AuditLogEntity> findByActorId(UUID actorId);
    
    List<AuditLogEntity> findBySubjectId(UUID subjectId);
    
    List<AuditLogEntity> findByEventType(String eventType);
    
    List<AuditLogEntity> findByCorrelationId(String correlationId);
    
    @Query("SELECT a FROM AuditLogEntity a WHERE a.timestamp BETWEEN :startTime AND :endTime")
    List<AuditLogEntity> findByTimestampBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    List<AuditLogEntity> findByResource(String resource);
}

