package me.namila.service.auth.domain.application.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for user profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private UUID profileId;
    private String firstName;
    private String lastName;
    private String displayName;
    private Map<String, Object> attributes;
    private Instant createdAt;
    private Instant lastModifiedAt;
}

