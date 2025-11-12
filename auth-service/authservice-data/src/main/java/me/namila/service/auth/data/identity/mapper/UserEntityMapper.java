package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.UserJpaEntity;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * MapStruct mapper for User Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {
    
    @Mapping(target = "id", source = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "username", source = "username", qualifiedByName = "stringToUsernameValue")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmailValue")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToUserStatus")
    @Mapping(target = "federatedIdentities", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "updatedAt", source = "lastModifiedAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "version", ignore = true)
    UserAggregate toDomain(UserJpaEntity entity);
    
    @Mapping(target = "userId", source = "id.value")
    @Mapping(target = "username", source = "username", qualifiedByName = "usernameValueToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailValueToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    UserJpaEntity toEntity(UserAggregate domain);
    
    @Named("uuidToUserId")
    default UserId uuidToUserId(java.util.UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }
    
    @Named("stringToUsernameValue")
    default UsernameValue stringToUsernameValue(String username) {
        return username != null ? UsernameValue.of(username) : null;
    }
    
    @Named("usernameValueToString")
    default String usernameValueToString(UsernameValue username) {
        return username != null ? username.getValue() : null;
    }
    
    @Named("stringToEmailValue")
    default EmailValue stringToEmailValue(String email) {
        return email != null ? EmailValue.of(email) : null;
    }
    
    @Named("emailValueToString")
    default String emailValueToString(EmailValue email) {
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
    
    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
    
    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}

