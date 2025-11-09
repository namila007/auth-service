package me.namila.service.auth.domain.application.identity.mapper;

import me.namila.service.auth.domain.application.identity.dto.request.CreateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UpdateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UserProfileRequest;
import me.namila.service.auth.domain.application.identity.dto.response.*;
import me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.UserProfileEntity;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for User DTO-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "stringToUsername")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileRequestToProfile")
    @Mapping(target = "federatedIdentities", ignore = true)
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "version", ignore = true)
    UserAggregate toDomain(CreateUserRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileRequestToProfile")
    @Mapping(target = "federatedIdentities", ignore = true)
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "version", ignore = true)
    UserAggregate toDomain(UpdateUserRequest request);
    
    @Mapping(target = "userId", source = "id.value")
    @Mapping(target = "username", source = "username", qualifiedByName = "usernameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    UserResponse toResponse(UserAggregate domain);
    
    @Mapping(target = "userId", source = "id.value")
    @Mapping(target = "username", source = "username", qualifiedByName = "usernameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToString")
    @Mapping(target = "displayName", source = "profile.displayName")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    UserSummaryResponse toSummaryResponse(UserAggregate domain);
    
    @Mapping(target = "userId", source = "id.value")
    @Mapping(target = "username", source = "username", qualifiedByName = "usernameToString")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToString")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileToResponse")
    @Mapping(target = "federatedIdentities", source = "federatedIdentities", qualifiedByName = "federatedIdentitiesToResponse")
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "version", source = "version")
    UserDetailResponse toDetailResponse(UserAggregate domain);
    
    @Mapping(target = "profileId", source = "id.value")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    UserProfileResponse toProfileResponse(UserProfileEntity profile);
    
    @Mapping(target = "federatedIdentityId", source = "id.value")
    @Mapping(target = "providerName", ignore = true) // Will be populated from provider config
    FederatedIdentityResponse toFederatedIdentityResponse(FederatedIdentityEntity federatedIdentity);
    
    @Named("stringToUsername")
    default UsernameValue stringToUsername(String username) {
        return username != null ? UsernameValue.of(username) : null;
    }
    
    @Named("usernameToString")
    default String usernameToString(UsernameValue username) {
        return username != null ? username.getValue() : null;
    }
    
    @Named("stringToEmail")
    default EmailValue stringToEmail(String email) {
        return email != null ? EmailValue.of(email) : null;
    }
    
    @Named("emailToString")
    default String emailToString(EmailValue email) {
        return email != null ? email.getValue() : null;
    }
    
    @Named("userStatusToString")
    default String userStatusToString(me.namila.service.auth.domain.core.identity.valueobject.UserStatus status) {
        return status != null ? status.name() : null;
    }
    
    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
    
    @Named("profileRequestToProfile")
    default UserProfileEntity profileRequestToProfile(UserProfileRequest request) {
        if (request == null) {
            return null;
        }
        return UserProfileEntity.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .displayName(request.getDisplayName())
            .attributes(request.getAttributes())
            .build();
    }
    
    @Named("profileToResponse")
    default UserProfileResponse profileToResponse(UserProfileEntity profile) {
        if (profile == null) {
            return null;
        }
        return toProfileResponse(profile);
    }
    
    @Named("federatedIdentitiesToResponse")
    default List<FederatedIdentityResponse> federatedIdentitiesToResponse(List<FederatedIdentityEntity> federatedIdentities) {
        if (federatedIdentities == null) {
            return null;
        }
        return federatedIdentities.stream()
            .map(this::toFederatedIdentityResponse)
            .collect(Collectors.toList());
    }
}

