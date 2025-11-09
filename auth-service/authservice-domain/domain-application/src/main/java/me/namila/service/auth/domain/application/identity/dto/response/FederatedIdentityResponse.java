package me.namila.service.auth.domain.application.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for federated identity information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FederatedIdentityResponse {
    
    private UUID federatedIdentityId;
    private UUID providerId;
    private String providerName;
    private String subjectId;
    private String issuer;
    private Instant linkedAt;
    private Instant lastSyncedAt;
}

