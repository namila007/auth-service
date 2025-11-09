package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.PolicyEntity;
import me.namila.service.auth.domain.core.authorization.model.Policy;
import me.namila.service.auth.domain.core.authorization.valueobject.Effect;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Policy Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PolicyEntityMapper {
    
    @Mapping(target = "policyType", source = "policyType", qualifiedByName = "stringToPolicyType")
    @Mapping(target = "effect", source = "effect", qualifiedByName = "stringToEffect")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "listToSet")
    Policy toDomain(PolicyEntity entity);
    
    @Mapping(target = "policyType", source = "policyType", qualifiedByName = "policyTypeToString")
    @Mapping(target = "effect", source = "effect", qualifiedByName = "effectToString")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "setToList")
    PolicyEntity toEntity(Policy domain);
    
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
}

