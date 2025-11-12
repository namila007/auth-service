package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.PermissionJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * MapStruct mapper for Permission Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionEntityMapper {
    
    @Mapping(target = "id", source = "permissionId", qualifiedByName = "uuidToPermissionId")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "updatedAt", ignore = true)
    me.namila.service.auth.domain.core.authorization.model.PermissionEntity toDomain(PermissionJpaEntity entity);
    
    @Mapping(target = "permissionId", source = "id.value")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    PermissionJpaEntity toEntity(me.namila.service.auth.domain.core.authorization.model.PermissionEntity domain);
    
    @Named("uuidToPermissionId")
    default PermissionId uuidToPermissionId(java.util.UUID uuid) {
        return uuid != null ? PermissionId.of(uuid) : null;
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

