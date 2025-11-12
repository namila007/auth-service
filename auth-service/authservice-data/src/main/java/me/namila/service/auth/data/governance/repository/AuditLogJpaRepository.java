package me.namila.service.auth.data.governance.repository;

import me.namila.service.auth.data.governance.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for AuditLogJpaEntity.
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
    
    List<AuditLogJpaEntity> findByActorId(UUID actorId);
    
    List<AuditLogJpaEntity> findBySubjectId(UUID subjectId);
    
    List<AuditLogJpaEntity> findByEventType(String eventType);
    
    List<AuditLogJpaEntity> findByCorrelationId(String correlationId);
    
    @Query("SELECT a FROM AuditLogJpaEntity a WHERE a.timestamp BETWEEN :startTime AND :endTime")
    List<AuditLogJpaEntity> findByTimestampBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    List<AuditLogJpaEntity> findByResource(String resource);
}

