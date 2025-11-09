package me.namila.service.auth.domain.core.identity.service;

import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDomainService.
 * Tests business logic in isolation without Spring context.
 */
@DisplayName("UserDomainService Unit Tests")
class UserDomainServiceTest {
    
    private UserDomainService userDomainService;
    
    @BeforeEach
    void setUp() {
        userDomainService = new UserDomainService();
    }
    
    @Test
    @DisplayName("validateUserActivation_ActiveUser_ShouldNotThrowException")
    void validateUserActivation_ActiveUser_ShouldNotThrowException() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.ACTIVE)
            .build();
        
        // When & Then
        assertDoesNotThrow(() -> userDomainService.validateUserActivation(user));
    }
    
    @Test
    @DisplayName("validateUserActivation_PendingVerificationUser_ShouldNotThrowException")
    void validateUserActivation_PendingVerificationUser_ShouldNotThrowException() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.PENDING_VERIFICATION)
            .build();
        
        // When & Then
        assertDoesNotThrow(() -> userDomainService.validateUserActivation(user));
    }
    
    @Test
    @DisplayName("validateUserActivation_SuspendedUser_ShouldThrowException")
    void validateUserActivation_SuspendedUser_ShouldThrowException() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.SUSPENDED)
            .build();
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> userDomainService.validateUserActivation(user)
        );
        
        assertEquals("Cannot activate a suspended user", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateUserActivation_LockedUser_ShouldThrowException")
    void validateUserActivation_LockedUser_ShouldThrowException() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.LOCKED)
            .build();
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> userDomainService.validateUserActivation(user)
        );
        
        assertEquals("Cannot activate a locked user. Unlock first.", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateUserActivation_NullUser_ShouldThrowException")
    void validateUserActivation_NullUser_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userDomainService.validateUserActivation(null)
        );
        
        assertEquals("User cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateUniqueCredentials_ValidCredentials_ShouldNotThrowException")
    void validateUniqueCredentials_ValidCredentials_ShouldNotThrowException() {
        // Given
        UsernameValue username = UsernameValue.of("testuser");
        EmailValue email = EmailValue.of("test@example.com");
        
        // When & Then
        assertDoesNotThrow(() -> userDomainService.validateUniqueCredentials(username, email));
    }
    
    @Test
    @DisplayName("validateUniqueCredentials_NullUsername_ShouldThrowException")
    void validateUniqueCredentials_NullUsername_ShouldThrowException() {
        // Given
        EmailValue email = EmailValue.of("test@example.com");
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userDomainService.validateUniqueCredentials(null, email)
        );
        
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateUniqueCredentials_NullEmail_ShouldThrowException")
    void validateUniqueCredentials_NullEmail_ShouldThrowException() {
        // Given
        UsernameValue username = UsernameValue.of("testuser");
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userDomainService.validateUniqueCredentials(username, null)
        );
        
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("canDeleteUser_ActiveUser_ShouldReturnFalse")
    void canDeleteUser_ActiveUser_ShouldReturnFalse() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.ACTIVE)
            .build();
        
        // When
        boolean result = userDomainService.canDeleteUser(user);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("canDeleteUser_SuspendedUser_ShouldReturnTrue")
    void canDeleteUser_SuspendedUser_ShouldReturnTrue() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.SUSPENDED)
            .build();
        
        // When
        boolean result = userDomainService.canDeleteUser(user);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    @DisplayName("canDeleteUser_LockedUser_ShouldReturnTrue")
    void canDeleteUser_LockedUser_ShouldReturnTrue() {
        // Given
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of("testuser"))
            .email(EmailValue.of("test@example.com"))
            .status(UserStatus.LOCKED)
            .build();
        
        // When
        boolean result = userDomainService.canDeleteUser(user);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    @DisplayName("canDeleteUser_NullUser_ShouldReturnFalse")
    void canDeleteUser_NullUser_ShouldReturnFalse() {
        // When
        boolean result = userDomainService.canDeleteUser(null);
        
        // Then
        assertFalse(result);
    }
}

