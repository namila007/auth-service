package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.UserEntity;
import me.namila.service.auth.domain.core.identity.model.User;
import me.namila.service.auth.domain.core.identity.valueobject.Email;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.Username;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for User Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {
    
    @Mapping(target = "username", source = "username", qualifiedByName = "stringToUsername")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToUserStatus")
    @Mapping(target = "federatedIdentities", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toDomain(UserEntity entity);
    
    @Mapping(target = "username", source = "username", qualifiedByName = "usernameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToString")
    UserEntity toEntity(User domain);
    
    @Named("stringToUsername")
    default Username stringToUsername(String username) {
        return username != null ? Username.of(username) : null;
    }
    
    @Named("usernameToString")
    default String usernameToString(Username username) {
        return username != null ? username.getValue() : null;
    }
    
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? Email.of(email) : null;
    }
    
    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }
    
    @Named("stringToUserStatus")
    default UserStatus stringToUserStatus(String status) {
        return status != null ? UserStatus.valueOf(status) : null;
    }
    
    @Named("userStatusToString")
    default String userStatusToString(UserStatus status) {
        return status != null ? status.name() : null;
    }
}

