package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.UserRoleAssignmentEntity;
import me.namila.service.auth.data.identity.entity.UserEntity;
import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignment;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for UserRoleAssignment Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleAssignmentEntityMapper {
    
    @Mapping(target = "scope", source = "scope", qualifiedByName = "stringToAssignmentScope")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToAssignmentStatus")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "roleId", source = "role.roleId")
    UserRoleAssignment toDomain(UserRoleAssignmentEntity entity);
    
    @Mapping(target = "scope", source = "scope", qualifiedByName = "assignmentScopeToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "assignmentStatusToString")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserRoleAssignmentEntity toEntity(UserRoleAssignment domain);
    
    @Named("stringToAssignmentScope")
    default AssignmentScope stringToAssignmentScope(String scope) {
        return scope != null ? AssignmentScope.valueOf(scope) : null;
    }
    
    @Named("assignmentScopeToString")
    default String assignmentScopeToString(AssignmentScope scope) {
        return scope != null ? scope.name() : null;
    }
    
    @Named("stringToAssignmentStatus")
    default AssignmentStatus stringToAssignmentStatus(String status) {
        return status != null ? AssignmentStatus.valueOf(status) : null;
    }
    
    @Named("assignmentStatusToString")
    default String assignmentStatusToString(AssignmentStatus status) {
        return status != null ? status.name() : null;
    }
}

