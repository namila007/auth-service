package me.namila.service.auth.domain.application.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for user summary (minimal data for list views).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    
    private UUID userId;
    private String username;
    private String email;
    private String status;
    private String displayName;
    private Instant createdAt;
}

