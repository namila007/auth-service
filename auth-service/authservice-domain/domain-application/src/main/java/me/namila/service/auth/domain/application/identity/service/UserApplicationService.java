package me.namila.service.auth.domain.application.identity.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.dto.response.PagedResponse;
import me.namila.service.auth.domain.application.identity.dto.request.CreateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UpdateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.response.*;
import me.namila.service.auth.domain.application.identity.mapper.UserDtoMapper;
import me.namila.service.auth.domain.application.port.identity.UserRepositoryPort;
import me.namila.service.auth.domain.core.exception.DuplicateEntityException;
import me.namila.service.auth.domain.core.exception.UserNotFoundException;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.UserProfileEntity;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for user management use cases.
 * Orchestrates domain operations and handles transaction boundaries.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService {
    
    private final UserRepositoryPort userRepository;
    private final UserDtoMapper userDtoMapper;
    
    /**
     * Create a new user.
     * @param request The create user request
     * @return The created user response
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Validate uniqueness
        UsernameValue username = UsernameValue.of(request.getUsername());
        EmailValue email = EmailValue.of(request.getEmail());
        
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateEntityException("User", "username", request.getUsername());
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEntityException("User", "email", request.getEmail());
        }
        
        // Map request to domain model
        UserAggregate user = userDtoMapper.toDomain(request);
        user.setStatus(UserStatus.ACTIVE); // Default to active
        
        // Save user
        UserAggregate savedUser = userRepository.save(user);
        
        // Map to response
        return userDtoMapper.toResponse(savedUser);
    }
    
    /**
     * Get user by ID.
     * @param userId The user ID
     * @return The user detail response
     */
    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(UUID userId) {
        UserAggregate user = userRepository.findById(UserId.of(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return userDtoMapper.toDetailResponse(user);
    }
    
    /**
     * Update user.
     * @param userId The user ID
     * @param request The update user request
     * @return The updated user detail response
     */
    @Transactional
    public UserDetailResponse updateUser(UUID userId, UpdateUserRequest request) {
        UserAggregate user = userRepository.findById(UserId.of(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Update email if provided
        if (request.getEmail() != null) {
            EmailValue newEmail = EmailValue.of(request.getEmail());
            if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
                throw new DuplicateEntityException("User", "email", request.getEmail());
            }
            user.updateEmail(newEmail);
        }
        
        // Update profile if provided
        if (request.getProfile() != null) {
            UserProfileEntity profile = userDtoMapper.toDomain(request).getProfile();
            if (profile != null) {
                user.updateProfile(profile);
            }
        }
        
        // Update metadata if provided
        if (request.getMetadata() != null) {
            user.setMetadata(request.getMetadata());
        }
        
        // Save updated user
        UserAggregate savedUser = userRepository.save(user);
        
        return userDtoMapper.toDetailResponse(savedUser);
    }
    
    /**
     * List users with pagination.
     * @param pageable Pagination parameters
     * @return Paged response with user summaries
     */
    @Transactional(readOnly = true)
    public PagedResponse<UserSummaryResponse> listUsers(Pageable pageable) {
        // Note: This is a simplified implementation
        // In a real scenario, you would use Spring Data JPA's Pageable support
        // For now, we'll fetch all and paginate in memory (not ideal for production)
        var allUsers = userRepository.findAll();
        
        // Simple pagination (should be done at repository level in production)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allUsers.size());
        var pagedUsers = allUsers.subList(start, end);
        
        var summaries = pagedUsers.stream()
            .map(userDtoMapper::toSummaryResponse)
            .toList();
        
        // Create a simple page response
        // In production, use Spring Data's Page implementation
        PagedResponse<UserSummaryResponse> response = new PagedResponse<>();
        response.setContent(summaries);
        response.setPage(pageable.getPageNumber());
        response.setSize(pageable.getPageSize());
        response.setTotalElements(allUsers.size());
        response.setTotalPages((int) Math.ceil((double) allUsers.size() / pageable.getPageSize()));
        response.setFirst(pageable.getPageNumber() == 0);
        response.setLast(end >= allUsers.size());
        response.setHasNext(end < allUsers.size());
        response.setHasPrevious(pageable.getPageNumber() > 0);
        response.setEmpty(summaries.isEmpty());
        
        return response;
    }
    
    /**
     * Delete or deactivate user.
     * @param userId The user ID
     */
    @Transactional
    public void deleteUser(UUID userId) {
        UserAggregate user = userRepository.findById(UserId.of(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Soft delete: deactivate the user
        user.suspend();
        userRepository.save(user);
        
        // Or hard delete:
        // userRepository.deleteById(UserId.of(userId));
    }
    
    /**
     * Activate user.
     * @param userId The user ID
     * @return The activated user response
     */
    @Transactional
    public UserResponse activateUser(UUID userId) {
        UserAggregate user = userRepository.findById(UserId.of(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.activate();
        UserAggregate savedUser = userRepository.save(user);
        
        return userDtoMapper.toResponse(savedUser);
    }
}

