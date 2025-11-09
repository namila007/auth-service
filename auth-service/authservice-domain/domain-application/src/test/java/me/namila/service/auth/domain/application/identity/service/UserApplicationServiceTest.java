package me.namila.service.auth.domain.application.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import me.namila.service.auth.domain.application.dto.response.PagedResponse;
import me.namila.service.auth.domain.application.identity.dto.request.CreateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UpdateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UserProfileRequest;
import me.namila.service.auth.domain.application.identity.dto.response.UserDetailResponse;
import me.namila.service.auth.domain.application.identity.dto.response.UserResponse;
import me.namila.service.auth.domain.application.identity.dto.response.UserSummaryResponse;
import me.namila.service.auth.domain.application.identity.mapper.UserDtoMapper;
import me.namila.service.auth.domain.application.port.identity.UserRepositoryPort;
import me.namila.service.auth.domain.core.exception.DuplicateEntityException;
import me.namila.service.auth.domain.core.exception.UserNotFoundException;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.UserProfileEntity;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for UserApplicationService.
 * Tests application service logic with mocked dependencies.
 */

@ExtendWith(SpringExtension.class)
@DisplayName("UserApplicationService Unit Tests")
class UserApplicationServiceTest
{

  @Mock
  private UserRepositoryPort userRepository;

  @Mock
  private UserDtoMapper userDtoMapper;

  @InjectMocks
  private UserApplicationService userApplicationService;

  private UserAggregate testUser;

  private UserId testUserId;

  private UUID testUserIdUuid;

  @BeforeEach
  void setUp()
  {
    //    userRepository = org.mockito.Mockito.mock(UserRepositoryPort.class,RETURNS_DEEP_STUBS);
    //    userDtoMapper = org.mockito.Mockito.mock(UserDtoMapper.class,RETURNS_DEEP_STUBS);
    //    userApplicationService = new UserApplicationService(userRepository, userDtoMapper);
    testUserIdUuid = UUID.randomUUID();
    testUserId = UserId.of(testUserIdUuid);
    testUser = UserAggregate.builder().id(testUserId).username(UsernameValue.of("testuser"))
        .email(EmailValue.of("test@example.com")).status(UserStatus.ACTIVE).version(0L).createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now()).build();

  }

  @Test
  @DisplayName("createUser_ValidRequest_ShouldCreateAndReturnUser")
  void createUser_ValidRequest_ShouldCreateAndReturnUser()
  {
    // Given
    CreateUserRequest request = CreateUserRequest.builder().username("newuser").email("newuser@example.com").build();

    UserAggregate newUser = UserAggregate.builder().id(UserId.generate()).username(UsernameValue.of("newuser"))
        .email(EmailValue.of("newuser@example.com")).status(UserStatus.ACTIVE).build();

    UserResponse response = UserResponse.builder().userId(newUser.getId().getValue()).username("newuser")
        .email("newuser@example.com").status("ACTIVE").build();

    when(userRepository.existsByUsername(any(UsernameValue.class))).thenReturn(false);
    when(userRepository.existsByEmail(any(EmailValue.class))).thenReturn(false);
    when(userDtoMapper.toDomain(request)).thenReturn(newUser);
    when(userRepository.save(any(UserAggregate.class))).thenReturn(newUser);
    when(userDtoMapper.toResponse(newUser)).thenReturn(response);

    // When
    UserResponse result = userApplicationService.createUser(request);

    // Then
    assertNotNull(result);
    assertEquals("newuser", result.getUsername());
    assertEquals("newuser@example.com", result.getEmail());
    verify(userRepository).existsByUsername(any(UsernameValue.class));
    verify(userRepository).existsByEmail(any(EmailValue.class));
    verify(userRepository).save(any(UserAggregate.class));
    verify(userDtoMapper).toResponse(newUser);
  }

  @Test
  @DisplayName("createUser_DuplicateUsername_ShouldThrowException")
  void createUser_DuplicateUsername_ShouldThrowException()
  {
    // Given
    CreateUserRequest request = CreateUserRequest.builder().username("existinguser").email("newuser@example.com")
        .build();

    when(userRepository.existsByUsername(any(UsernameValue.class))).thenReturn(true);

    // When & Then
    DuplicateEntityException exception = assertThrows(DuplicateEntityException.class,
        () -> userApplicationService.createUser(request));

    assertTrue(exception.getMessage().contains("username"));
    verify(userRepository).existsByUsername(any(UsernameValue.class));
    verify(userRepository, never()).save(any(UserAggregate.class));
  }

  @Test
  @DisplayName("createUser_DuplicateEmail_ShouldThrowException")
  void createUser_DuplicateEmail_ShouldThrowException()
  {
    // Given
    CreateUserRequest request = CreateUserRequest.builder().username("newuser").email("existing@example.com").build();

    when(userRepository.existsByUsername(any(UsernameValue.class))).thenReturn(false);
    when(userRepository.existsByEmail(any(EmailValue.class))).thenReturn(true);

    // When & Then
    DuplicateEntityException exception = assertThrows(DuplicateEntityException.class,
        () -> userApplicationService.createUser(request));

    assertTrue(exception.getMessage().contains("email"));
    verify(userRepository).existsByEmail(any(EmailValue.class));
    verify(userRepository, never()).save(any(UserAggregate.class));
  }

  @Test
  @DisplayName("getUserById_ExistingUser_ShouldReturnUserDetail")
  void getUserById_ExistingUser_ShouldReturnUserDetail()
  {
    // Given
    UserDetailResponse response = UserDetailResponse.builder().userId(testUserIdUuid).username("testuser")
        .email("test@example.com").status("ACTIVE").build();

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(userDtoMapper.toDetailResponse(testUser)).thenReturn(response);

    // When
    UserDetailResponse result = userApplicationService.getUserById(testUserIdUuid);

    // Then
    assertNotNull(result);
    assertEquals(testUserIdUuid, result.getUserId());
    assertEquals("testuser", result.getUsername());
    verify(userRepository).findById(testUserId);
    verify(userDtoMapper).toDetailResponse(testUser);
  }

  @Test
  @DisplayName("getUserById_NonExistingUser_ShouldThrowException")
  void getUserById_NonExistingUser_ShouldThrowException()
  {
    // Given
    UUID nonExistingId = UUID.randomUUID();
    when(userRepository.findById(UserId.of(nonExistingId))).thenReturn(Optional.empty());

    // When & Then
    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        () -> userApplicationService.getUserById(nonExistingId));

    assertTrue(exception.getMessage().contains(nonExistingId.toString()));
    verify(userRepository).findById(UserId.of(nonExistingId));
    verify(userDtoMapper, never()).toDetailResponse(any(UserAggregate.class));
  }

  @Test
  @DisplayName("updateUser_ValidRequest_ShouldUpdateAndReturnUser")
  void updateUser_ValidRequest_ShouldUpdateAndReturnUser()
  {
    // Given
    UpdateUserRequest request = UpdateUserRequest.builder().email("updated@example.com").build();

    EmailValue updatedEmail = EmailValue.of("updated@example.com");

    UserAggregate updatedUser = UserAggregate.builder().id(testUserId).username(UsernameValue.of("testuser"))
        .email(updatedEmail).status(UserStatus.ACTIVE).build();

    UserDetailResponse response = UserDetailResponse.builder().userId(testUserIdUuid).email("updated@example.com")
        .build();

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    // Email doesn't exist or is the same as current email
    when(userRepository.existsByEmail(any(EmailValue.class))).thenReturn(false);
    when(userRepository.save(any(UserAggregate.class))).thenAnswer(invocation -> {
      UserAggregate saved = invocation.getArgument(0);
      return updatedUser;
    });
    when(userDtoMapper.toDetailResponse(any(UserAggregate.class))).thenReturn(response);

    // When
    UserDetailResponse result = userApplicationService.updateUser(testUserIdUuid, request);

    // Then
    assertNotNull(result);
    assertEquals("updated@example.com", result.getEmail());
    verify(userRepository).findById(testUserId);
    verify(userRepository).existsByEmail(any(EmailValue.class));
    verify(userRepository).save(any(UserAggregate.class));
    verify(userDtoMapper).toDetailResponse(any(UserAggregate.class));
  }

  @Test
  @DisplayName("updateUser_WithProfile_ShouldUpdateProfile")
  void updateUser_WithProfile_ShouldUpdateProfile()
  {
    // Given
    UserProfileRequest profileRequest = UserProfileRequest.builder().firstName("John").lastName("Doe")
        .displayName("John Doe").build();

    UpdateUserRequest request = UpdateUserRequest.builder().profile(profileRequest).build();

    UserProfileEntity profile = UserProfileEntity.builder().firstName("John").lastName("Doe").displayName("John Doe")
        .build();

    // The mapper returns a UserAggregate with the profile
    UserAggregate mappedUser = UserAggregate.builder().id(testUserId).username(UsernameValue.of("testuser"))
        .email(EmailValue.of("test@example.com")).profile(profile).build();

    // After update, the user should have the profile
    UserAggregate updatedUser = UserAggregate.builder().id(testUserId).username(UsernameValue.of("testuser"))
        .email(EmailValue.of("test@example.com")).profile(profile).status(UserStatus.ACTIVE).build();

    UserDetailResponse response = UserDetailResponse.builder().userId(testUserIdUuid).profile(
        me.namila.service.auth.domain.application.identity.dto.response.UserProfileResponse.builder().firstName("John")
            .lastName("Doe").displayName("John Doe").build()).build();

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(userDtoMapper.toDomain(request)).thenReturn(mappedUser);
    when(userRepository.save(any(UserAggregate.class))).thenAnswer(invocation -> {
      UserAggregate saved = invocation.getArgument(0);
      return updatedUser;
    });
    when(userDtoMapper.toDetailResponse(any(UserAggregate.class))).thenReturn(response);

    // When
    UserDetailResponse result = userApplicationService.updateUser(testUserIdUuid, request);

    // Then
    assertNotNull(result);
    assertNotNull(result.getProfile());
    assertEquals("John", result.getProfile().getFirstName());
    verify(userRepository).findById(testUserId);
    verify(userDtoMapper).toDomain(request);
    verify(userRepository).save(any(UserAggregate.class));
    verify(userDtoMapper).toDetailResponse(any(UserAggregate.class));
  }

  @Test
  @DisplayName("updateUser_DuplicateEmail_ShouldThrowException")
  void updateUser_DuplicateEmail_ShouldThrowException()
  {
    // Given
    UpdateUserRequest request = UpdateUserRequest.builder().email("duplicate@example.com").build();

    EmailValue duplicateEmail = EmailValue.of("duplicate@example.com");

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    // The service checks: existsByEmail(newEmail) && !user.getEmail().equals(newEmail)
    // So we need to return true for existsByEmail and the current email is different
    when(userRepository.existsByEmail(duplicateEmail)).thenReturn(true);

    // When & Then
    DuplicateEntityException exception = assertThrows(DuplicateEntityException.class,
        () -> userApplicationService.updateUser(testUserIdUuid, request));

    assertTrue(exception.getMessage().contains("email"));
    verify(userRepository).findById(testUserId);
    verify(userRepository).existsByEmail(any(EmailValue.class));
    verify(userRepository, never()).save(any(UserAggregate.class));
  }

  @Test
  @DisplayName("listUsers_WithPagination_ShouldReturnPagedResponse")
  void listUsers_WithPagination_ShouldReturnPagedResponse()
  {
    // Given
    List<UserAggregate> users = Arrays.asList(testUser, testUser, testUser);
    Pageable pageable = PageRequest.of(0, 10);

    UserSummaryResponse summary = UserSummaryResponse.builder().userId(testUserIdUuid).username("testuser")
        .email("test@example.com").build();

    when(userRepository.findAll()).thenReturn(users);
    when(userDtoMapper.toSummaryResponse(any(UserAggregate.class))).thenReturn(summary);

    // When
    PagedResponse<UserSummaryResponse> result = userApplicationService.listUsers(pageable);

    // Then
    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(0, result.getPage());
    assertEquals(10, result.getSize());
    assertEquals(3, result.getTotalElements());
    verify(userRepository).findAll();
    verify(userDtoMapper, times(3)).toSummaryResponse(any(UserAggregate.class));
  }

  @Test
  @DisplayName("deleteUser_ExistingUser_ShouldDeactivateUser")
  void deleteUser_ExistingUser_ShouldDeactivateUser()
  {
    // Given
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(UserAggregate.class))).thenAnswer(invocation -> {
      UserAggregate saved = invocation.getArgument(0);
      // The suspend() method is called on the user, so verify it was called
      return saved;
    });

    // When
    userApplicationService.deleteUser(testUserIdUuid);

    // Then
    verify(userRepository).findById(testUserId);
    verify(userRepository).save(any(UserAggregate.class));
    // Verify that suspend was called (status should be SUSPENDED)
    assertEquals(UserStatus.SUSPENDED, testUser.getStatus());
  }

  @Test
  @DisplayName("deleteUser_NonExistingUser_ShouldThrowException")
  void deleteUser_NonExistingUser_ShouldThrowException()
  {
    // Given
    UUID nonExistingId = UUID.randomUUID();
    when(userRepository.findById(UserId.of(nonExistingId))).thenReturn(Optional.empty());

    // When & Then
    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        () -> userApplicationService.deleteUser(nonExistingId));

    assertTrue(exception.getMessage().contains(nonExistingId.toString()));
    verify(userRepository, never()).save(any(UserAggregate.class));
  }

  @Test
  @DisplayName("activateUser_ExistingUser_ShouldActivateAndReturnUser")
  void activateUser_ExistingUser_ShouldActivateAndReturnUser()
  {
    // Given - create a user with PENDING_VERIFICATION status
    UserAggregate pendingUser = UserAggregate.builder().id(testUserId).username(UsernameValue.of("testuser"))
        .email(EmailValue.of("test@example.com")).status(UserStatus.PENDING_VERIFICATION).build();

    UserAggregate activatedUser = UserAggregate.builder().id(testUserId).username(UsernameValue.of("testuser"))
        .email(EmailValue.of("test@example.com")).status(UserStatus.ACTIVE).build();

    UserResponse response = UserResponse.builder().userId(testUserIdUuid).username("testuser").email("test@example.com")
        .status("ACTIVE").build();

    when(userRepository.findById(testUserId)).thenReturn(Optional.of(pendingUser));
    when(userRepository.save(any(UserAggregate.class))).thenAnswer(invocation -> {
      UserAggregate saved = invocation.getArgument(0);
      // Return the activated user
      return activatedUser;
    });
    when(userDtoMapper.toResponse(any(UserAggregate.class))).thenReturn(response);

    // When
    UserResponse result = userApplicationService.activateUser(testUserIdUuid);

    // Then
    assertNotNull(result);
    assertEquals("ACTIVE", result.getStatus());
    verify(userRepository).findById(testUserId);
    verify(userRepository).save(any(UserAggregate.class));
    verify(userDtoMapper).toResponse(any(UserAggregate.class));
  }

  @Test
  @DisplayName("activateUser_NonExistingUser_ShouldThrowException")
  void activateUser_NonExistingUser_ShouldThrowException()
  {
    // Given
    UUID nonExistingId = UUID.randomUUID();
    when(userRepository.findById(UserId.of(nonExistingId))).thenReturn(Optional.empty());

    // When & Then
    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        () -> userApplicationService.activateUser(nonExistingId));

    assertTrue(exception.getMessage().contains(nonExistingId.toString()));
    verify(userRepository, never()).save(any(UserAggregate.class));
  }
}
