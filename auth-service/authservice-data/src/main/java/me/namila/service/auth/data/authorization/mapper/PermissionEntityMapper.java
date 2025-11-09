package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Permission Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionEntityMapper {
    
    Permission toDomain(PermissionEntity entity);
    
    PermissionEntity toEntity(Permission domain);
}

