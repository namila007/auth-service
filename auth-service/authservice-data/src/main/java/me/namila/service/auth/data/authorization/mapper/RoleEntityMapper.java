package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.RoleEntity;
import me.namila.service.auth.domain.core.authorization.model.Role;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Role Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleEntityMapper {
    
    @Mapping(target = "roleType", source = "roleType", qualifiedByName = "stringToRoleType")
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "parentRoles", ignore = true)
    Role toDomain(RoleEntity entity);
    
    @Mapping(target = "roleType", source = "roleType", qualifiedByName = "roleTypeToString")
    RoleEntity toEntity(Role domain);
    
    @Named("stringToRoleType")
    default RoleType stringToRoleType(String roleType) {
        return roleType != null ? RoleType.valueOf(roleType) : null;
    }
    
    @Named("roleTypeToString")
    default String roleTypeToString(RoleType roleType) {
        return roleType != null ? roleType.name() : null;
    }
}

