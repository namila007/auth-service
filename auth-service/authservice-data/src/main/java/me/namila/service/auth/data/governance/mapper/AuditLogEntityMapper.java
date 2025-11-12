package me.namila.service.auth.data.governance.mapper;

import me.namila.service.auth.data.governance.entity.AuditLogJpaEntity;
import me.namila.service.auth.domain.core.governance.model.id.AuditLogId;
import me.namila.service.auth.domain.core.governance.valueobject.ActorType;
import me.namila.service.auth.domain.core.governance.valueobject.AuditEventType;
import me.namila.service.auth.domain.core.governance.valueobject.Decision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for AuditLog Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogEntityMapper {
    
    @Mapping(target = "id", source = "auditId", qualifiedByName = "uuidToAuditLogId")
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "stringToAuditEventType")
    @Mapping(target = "actorType", source = "actorType", qualifiedByName = "stringToActorType")
    @Mapping(target = "decision", source = "decision", qualifiedByName = "stringToDecision")
    me.namila.service.auth.domain.core.governance.model.AuditLogEntity toDomain(AuditLogJpaEntity entity);
    
    @Mapping(target = "auditId", source = "id.value")
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "auditEventTypeToString")
    @Mapping(target = "actorType", source = "actorType", qualifiedByName = "actorTypeToString")
    @Mapping(target = "decision", source = "decision", qualifiedByName = "decisionToString")
    AuditLogJpaEntity toEntity(me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain);
    
    @Named("stringToAuditEventType")
    default AuditEventType stringToAuditEventType(String eventType) {
        return eventType != null ? AuditEventType.valueOf(eventType) : null;
    }
    
    @Named("auditEventTypeToString")
    default String auditEventTypeToString(AuditEventType eventType) {
        return eventType != null ? eventType.name() : null;
    }
    
    @Named("stringToActorType")
    default ActorType stringToActorType(String actorType) {
        return actorType != null ? ActorType.valueOf(actorType) : null;
    }
    
    @Named("actorTypeToString")
    default String actorTypeToString(ActorType actorType) {
        return actorType != null ? actorType.name() : null;
    }
    
    @Named("stringToDecision")
    default Decision stringToDecision(String decision) {
        return decision != null ? Decision.valueOf(decision) : null;
    }
    
    @Named("decisionToString")
    default String decisionToString(Decision decision) {
        return decision != null ? decision.name() : null;
    }
    
    @Named("uuidToAuditLogId")
    default AuditLogId uuidToAuditLogId(java.util.UUID uuid) {
        return uuid != null ? AuditLogId.of(uuid) : null;
    }
}

