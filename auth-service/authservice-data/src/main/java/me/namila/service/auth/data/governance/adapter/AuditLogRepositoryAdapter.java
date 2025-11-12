package me.namila.service.auth.data.governance.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.governance.mapper.AuditLogEntityMapper;
import me.namila.service.auth.data.governance.repository.AuditLogJpaRepository;
import me.namila.service.auth.domain.application.port.governance.AuditLogRepositoryPort;
import me.namila.service.auth.domain.core.governance.model.AuditLogEntity;
import me.namila.service.auth.domain.core.governance.model.id.AuditLogId;
import me.namila.service.auth.domain.core.governance.valueobject.AuditEventType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter for AuditLog entity.
 * Implements AuditLogRepositoryPort using JPA.
 */
@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {
    
    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogEntityMapper mapper;
    
    @Override
    public AuditLogEntity save(AuditLogEntity auditLog) {
        var entity = mapper.toEntity(auditLog);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<AuditLogEntity> findById(AuditLogId auditId) {
        return jpaRepository.findById(auditId.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<AuditLogEntity> findByActorId(UUID actorId) {
        return jpaRepository.findByActorId(actorId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLogEntity> findBySubjectId(UUID subjectId) {
        return jpaRepository.findBySubjectId(subjectId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLogEntity> findByEventType(AuditEventType eventType) {
        return jpaRepository.findByEventType(eventType.name()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLogEntity> findByCorrelationId(String correlationId) {
        return jpaRepository.findByCorrelationId(correlationId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLogEntity> findByTimestampBetween(Instant startTime, Instant endTime) {
        return jpaRepository.findByTimestampBetween(startTime, endTime).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLogEntity> findByResource(String resource) {
        return jpaRepository.findByResource(resource).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}

