package me.namila.service.auth.domain.application.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for user information (basic).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private UUID userId;
    private String username;
    private String email;
    private String status;
    private Instant createdAt;
    private Instant lastModifiedAt;
}

