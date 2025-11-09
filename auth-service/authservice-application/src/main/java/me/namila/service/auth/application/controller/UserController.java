package me.namila.service.auth.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.dto.response.PagedResponse;
import me.namila.service.auth.domain.application.identity.dto.request.CreateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UpdateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.response.UserDetailResponse;
import me.namila.service.auth.domain.application.identity.dto.response.UserResponse;
import me.namila.service.auth.domain.application.identity.dto.response.UserSummaryResponse;
import me.namila.service.auth.domain.application.identity.service.UserApplicationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user management endpoints.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userApplicationService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves detailed information about a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDetailResponse> getUserById(
        @Parameter(description = "User ID", required = true)
        @PathVariable UUID userId) {
        UserDetailResponse response = userApplicationService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}")
    @Operation(summary = "Update user", description = "Updates user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UserDetailResponse> updateUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable UUID userId,
        @Valid @RequestBody UpdateUserRequest request) {
        UserDetailResponse response = userApplicationService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "List users", description = "Retrieves a paginated list of users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    public ResponseEntity<PagedResponse<UserSummaryResponse>> listUsers(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Sort field and direction", example = "createdAt,desc")
        @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        PagedResponse<UserSummaryResponse> response = userApplicationService.listUsers(pageable);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Deletes or deactivates a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable UUID userId) {
        userApplicationService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{userId}/activate")
    @Operation(summary = "Activate user", description = "Activates a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> activateUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable UUID userId) {
        UserResponse response = userApplicationService.activateUser(userId);
        return ResponseEntity.ok(response);
    }
}

