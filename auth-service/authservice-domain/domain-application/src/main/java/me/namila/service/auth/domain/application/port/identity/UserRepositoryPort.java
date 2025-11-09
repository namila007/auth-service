package me.namila.service.auth.domain.application.port.identity;

import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for User aggregate root.
 * This is a port interface in the domain-application layer.
 */
public interface UserRepositoryPort {
    
    /**
     * Save or update a user.
     * @param user The user to save
     * @return The saved user
     */
    UserAggregate save(UserAggregate user);
    
    /**
     * Find a user by ID.
     * @param userId The user ID
     * @return Optional containing the user if found
     */
    Optional<UserAggregate> findById(UserId userId);
    
    /**
     * Find a user by username.
     * @param username The username
     * @return Optional containing the user if found
     */
    Optional<UserAggregate> findByUsername(UsernameValue username);
    
    /**
     * Find a user by email.
     * @param email The email
     * @return Optional containing the user if found
     */
    Optional<UserAggregate> findByEmail(EmailValue email);
    
    /**
     * Check if a username exists.
     * @param username The username
     * @return true if username exists
     */
    boolean existsByUsername(UsernameValue username);
    
    /**
     * Check if an email exists.
     * @param email The email
     * @return true if email exists
     */
    boolean existsByEmail(EmailValue email);
    
    /**
     * Find all users.
     * @return List of all users
     */
    List<UserAggregate> findAll();
    
    /**
     * Delete a user by ID.
     * @param userId The user ID
     */
    void deleteById(UserId userId);
    
    /**
     * Find users by status.
     * @param status The user status
     * @return List of users with the given status
     */
    List<UserAggregate> findByStatus(me.namila.service.auth.domain.core.identity.valueobject.UserStatus status);
}

