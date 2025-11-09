package me.namila.service.auth.domain.application.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for user details (complete information).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    
    private UUID userId;
    private String username;
    private String email;
    private String status;
    
    private UserProfileResponse profile;
    
    private List<FederatedIdentityResponse> federatedIdentities;
    
    private Map<String, Object> metadata;
    
    private Instant createdAt;
    private Instant lastModifiedAt;
    private Long version;
}

