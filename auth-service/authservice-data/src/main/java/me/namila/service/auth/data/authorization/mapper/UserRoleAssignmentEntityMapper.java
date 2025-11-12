package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.UserRoleAssignmentJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignmentAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.UserRoleAssignmentId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
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
    
    @Mapping(target = "id", source = "assignmentId", qualifiedByName = "uuidToUserRoleAssignmentId")
    @Mapping(target = "scope", source = "scope", qualifiedByName = "stringToAssignmentScope")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToAssignmentStatus")
    // Extract IDs from FK columns (preferred - always works)
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "roleId", source = "roleId", qualifiedByName = "uuidToRoleId")
    @Mapping(target = "assignedBy", source = "assignedBy", qualifiedByName = "uuidToUserId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserRoleAssignmentAggregate toDomain(UserRoleAssignmentJpaEntity entity);
    
    @Mapping(target = "assignmentId", source = "id.value")
    @Mapping(target = "scope", source = "scope", qualifiedByName = "assignmentScopeToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "assignmentStatusToString")
    // Map IDs to FK columns
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "roleId", source = "roleId.value")
    @Mapping(target = "assignedBy", source = "assignedBy.value")
    // Never set relationships
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserRoleAssignmentJpaEntity toEntity(UserRoleAssignmentAggregate domain);
    
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
    
    @Named("uuidToUserRoleAssignmentId")
    default UserRoleAssignmentId uuidToUserRoleAssignmentId(java.util.UUID uuid) {
        return uuid != null ? UserRoleAssignmentId.of(uuid) : null;
    }
    
    @Named("uuidToUserId")
    default UserId uuidToUserId(java.util.UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }
    
    @Named("uuidToRoleId")
    default RoleId uuidToRoleId(java.util.UUID uuid) {
        return uuid != null ? RoleId.of(uuid) : null;
    }
}

