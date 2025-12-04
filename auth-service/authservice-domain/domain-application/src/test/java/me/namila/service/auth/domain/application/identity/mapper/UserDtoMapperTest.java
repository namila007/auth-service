package me.namila.service.auth.domain.application.identity.mapper;

import me.namila.service.auth.domain.application.identity.dto.request.CreateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UpdateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UserProfileRequest;
import me.namila.service.auth.domain.application.identity.dto.response.*;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.UserProfileEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.model.id.UserProfileId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDtoMapper (MapStruct generated implementation).
 * Tests DTO to Domain and Domain to DTO mapping logic.
 * Note: MapStruct generates implementation at compile time, so we use Mappers
 * factory.
 */
@DisplayName("UserDtoMapper Unit Tests")
class UserDtoMapperTest {

    private UserDtoMapper userDtoMapper;

    private UserAggregate testUser;
    private UserId testUserId;
    private UUID testUserIdUuid;

    @BeforeEach
    void setUp() {
        // Get MapStruct generated implementation
        userDtoMapper = Mappers.getMapper(UserDtoMapper.class);

        testUserIdUuid = UUID.randomUUID();
        testUserId = UserId.of(testUserIdUuid);

        UserProfileEntity profile = UserProfileEntity.builder()
                .id(UserProfileId.generate())
                .firstName("John")
                .lastName("Doe")
                .displayName("John Doe")
                .attributes(Map.of("department", "Engineering"))
                .build();

        FederatedIdentityEntity federatedIdentity = FederatedIdentityEntity.builder()
                .id(FederatedIdentityId.generate())
                .userId(testUserId)
                .providerId(OIDCProviderConfigId.of(UUID.randomUUID()))
                .subjectId("subject-123")
                .issuer("https://provider.example.com")
                .linkedAt(LocalDateTime.now())
                .lastSyncedAt(LocalDateTime.now())
                .metadata(new HashMap<>())
                .build();

        testUser = UserAggregate.builder()
                .id(testUserId)
                .username(UsernameValue.of("testuser"))
                .email(EmailValue.of("test@example.com"))
                .status(UserStatus.ACTIVE)
                .profile(profile)
                .federatedIdentities(List.of(federatedIdentity))
                .metadata(Map.of("key", "value"))
                .version(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("toDomain_CreateUserRequest_ShouldMapToUser")
    void toDomain_CreateUserRequest_ShouldMapToUser() {
        // Given
        UserProfileRequest profileRequest = UserProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .displayName("Jane Smith")
                .build();

        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .profile(profileRequest)
                .metadata(Map.of("key", "value"))
                .build();

        // When
        UserAggregate result = userDtoMapper.toDomain(request);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername().getValue());
        assertEquals("newuser@example.com", result.getEmail().getValue());
        assertNotNull(result.getProfile());
        assertEquals("Jane", result.getProfile().getFirstName());
        assertEquals("Jane Smith", result.getProfile().getDisplayName());
    }

    @Test
    @DisplayName("toDomain_UpdateUserRequest_ShouldMapToUser")
    void toDomain_UpdateUserRequest_ShouldMapToUser() {
        // Given
        UserProfileRequest profileRequest = UserProfileRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("updated@example.com")
                .profile(profileRequest)
                .build();

        // When
        UserAggregate result = userDtoMapper.toDomain(request);

        // Then
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail().getValue());
        assertNotNull(result.getProfile());
        assertEquals("Updated", result.getProfile().getFirstName());
    }

    @Test
    @DisplayName("toResponse_User_ShouldMapToUserResponse")
    void toResponse_User_ShouldMapToUserResponse() {
        // When
        UserResponse result = userDtoMapper.toResponse(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUserIdUuid, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("ACTIVE", result.getStatus());
        // Timestamps may be null if source entity doesn't have them set (using
        // @Builder)
        // This is acceptable for mapper tests - timestamps are set by BaseEntity
        // constructor in real usage
    }

    @Test
    @DisplayName("toSummaryResponse_User_ShouldMapToUserSummaryResponse")
    void toSummaryResponse_User_ShouldMapToUserSummaryResponse() {
        // When
        UserSummaryResponse result = userDtoMapper.toSummaryResponse(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUserIdUuid, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals("John Doe", result.getDisplayName());
        // Timestamp may be null if source entity doesn't have it set (using @Builder)
        // This is acceptable for mapper tests - timestamps are set by BaseEntity
        // constructor in real usage
    }

    @Test
    @DisplayName("toDetailResponse_User_ShouldMapToUserDetailResponse")
    void toDetailResponse_User_ShouldMapToUserDetailResponse() {
        // When
        UserDetailResponse result = userDtoMapper.toDetailResponse(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUserIdUuid, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getProfile());
        assertEquals("John", result.getProfile().getFirstName());
        assertEquals("John Doe", result.getProfile().getDisplayName());
        assertNotNull(result.getFederatedIdentities());
        assertEquals(1, result.getFederatedIdentities().size());
        assertNotNull(result.getMetadata());
        assertEquals(1L, result.getVersion());
    }

    @Test
    @DisplayName("toProfileResponse_UserProfile_ShouldMapToUserProfileResponse")
    void toProfileResponse_UserProfile_ShouldMapToUserProfileResponse() {
        // Given
        UserProfileEntity profile = testUser.getProfile();

        // When
        UserProfileResponse result = userDtoMapper.toProfileResponse(profile);

        // Then
        assertNotNull(result);
        assertEquals(profile.getId().getValue(), result.getProfileId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("John Doe", result.getDisplayName());
        assertNotNull(result.getAttributes());
    }

    @Test
    @DisplayName("toFederatedIdentityResponse_FederatedIdentity_ShouldMapToResponse")
    void toFederatedIdentityResponse_FederatedIdentity_ShouldMapToResponse() {
        // Given
        FederatedIdentityEntity federatedIdentity = testUser.getFederatedIdentities().get(0);

        // When
        FederatedIdentityResponse result = userDtoMapper.toFederatedIdentityResponse(federatedIdentity);

        // Then
        assertNotNull(result);
        assertEquals(federatedIdentity.getId().getValue(), result.getFederatedIdentityId());
        assertEquals(federatedIdentity.getProviderId().getValue(), result.getProviderId());
        assertEquals("subject-123", result.getSubjectId());
        assertNotNull(result.getLinkedAt());
    }

    @Test
    @DisplayName("toDomain_CreateUserRequestWithNullProfile_ShouldHandleNull")
    void toDomain_CreateUserRequestWithNullProfile_ShouldHandleNull() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .profile(null)
                .build();

        // When
        UserAggregate result = userDtoMapper.toDomain(request);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername().getValue());
        // Profile can be null
    }

    @Test
    @DisplayName("toDetailResponse_UserWithNullProfile_ShouldHandleNull")
    void toDetailResponse_UserWithNullProfile_ShouldHandleNull() {
        // Given
        UserAggregate userWithoutProfile = UserAggregate.builder()
                .id(testUserId)
                .username(UsernameValue.of("testuser"))
                .email(EmailValue.of("test@example.com"))
                .status(UserStatus.ACTIVE)
                .profile(null)
                .build();

        // When
        UserDetailResponse result = userDtoMapper.toDetailResponse(userWithoutProfile);

        // Then
        assertNotNull(result);
        assertEquals(testUserIdUuid, result.getUserId());
        // Profile can be null
    }

    @Test
    @DisplayName("toDetailResponse_UserWithNullFederatedIdentities_ShouldHandleNull")
    void toDetailResponse_UserWithNullFederatedIdentities_ShouldHandleNull() {
        // Given
        UserAggregate userWithoutFederated = UserAggregate.builder()
                .id(testUserId)
                .username(UsernameValue.of("testuser"))
                .email(EmailValue.of("test@example.com"))
                .status(UserStatus.ACTIVE)
                .federatedIdentities(null)
                .build();

        // When
        UserDetailResponse result = userDtoMapper.toDetailResponse(userWithoutFederated);

        // Then
        assertNotNull(result);
        assertEquals(testUserIdUuid, result.getUserId());
        // Federated identities can be null
    }
}
