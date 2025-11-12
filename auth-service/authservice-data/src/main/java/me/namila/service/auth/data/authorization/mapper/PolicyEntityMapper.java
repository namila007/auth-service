package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.PolicyJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.PolicyAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.PolicyId;
import me.namila.service.auth.domain.core.authorization.valueobject.Effect;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Policy Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PolicyEntityMapper {
    
    @Mapping(target = "id", source = "policyId", qualifiedByName = "uuidToPolicyId")
    @Mapping(target = "policyType", source = "policyType", qualifiedByName = "stringToPolicyType")
    @Mapping(target = "effect", source = "effect", qualifiedByName = "stringToEffect")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "listToSet")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "updatedAt", source = "lastModifiedAt", qualifiedByName = "instantToLocalDateTime")
    PolicyAggregate toDomain(PolicyJpaEntity entity);
    
    @Mapping(target = "policyId", source = "id.value")
    @Mapping(target = "policyType", source = "policyType", qualifiedByName = "policyTypeToString")
    @Mapping(target = "effect", source = "effect", qualifiedByName = "effectToString")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "setToList")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    PolicyJpaEntity toEntity(PolicyAggregate domain);
    
    @Named("stringToPolicyType")
    default PolicyType stringToPolicyType(String policyType) {
        return policyType != null ? PolicyType.valueOf(policyType) : null;
    }
    
    @Named("policyTypeToString")
    default String policyTypeToString(PolicyType policyType) {
        return policyType != null ? policyType.name() : null;
    }
    
    @Named("stringToEffect")
    default Effect stringToEffect(String effect) {
        return effect != null ? Effect.valueOf(effect) : null;
    }
    
    @Named("effectToString")
    default String effectToString(Effect effect) {
        return effect != null ? effect.name() : null;
    }
    
    @Named("listToSet")
    default Set<String> listToSet(List<String> list) {
        return list != null ? Set.copyOf(list) : null;
    }
    
    @Named("setToList")
    default List<String> setToList(Set<String> set) {
        return set != null ? List.copyOf(set) : null;
    }
    
    @Named("uuidToPolicyId")
    default PolicyId uuidToPolicyId(java.util.UUID uuid) {
        return uuid != null ? PolicyId.of(uuid) : null;
    }
    
    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
    
    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}

