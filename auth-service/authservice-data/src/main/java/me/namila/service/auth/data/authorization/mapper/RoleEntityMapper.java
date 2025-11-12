package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.RoleJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * MapStruct mapper for Role Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleEntityMapper {
    
    @Mapping(target = "id", source = "roleId", qualifiedByName = "uuidToRoleId")
    @Mapping(target = "roleType", source = "roleType", qualifiedByName = "stringToRoleType")
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "parentRoles", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "updatedAt", source = "lastModifiedAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "version", ignore = true)
    RoleAggregate toDomain(RoleJpaEntity entity);
    
    @Mapping(target = "roleId", source = "id.value")
    @Mapping(target = "roleType", source = "roleType", qualifiedByName = "roleTypeToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "version", ignore = true)
    RoleJpaEntity toEntity(RoleAggregate domain);
    
    @Named("uuidToRoleId")
    default RoleId uuidToRoleId(java.util.UUID uuid) {
        return uuid != null ? RoleId.of(uuid) : null;
    }
    
    @Named("stringToRoleType")
    default RoleType stringToRoleType(String roleType) {
        return roleType != null ? RoleType.valueOf(roleType) : null;
    }
    
    @Named("roleTypeToString")
    default String roleTypeToString(RoleType roleType) {
        return roleType != null ? roleType.name() : null;
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

