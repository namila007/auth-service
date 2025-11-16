package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * Response DTO for OIDC configuration details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCConfigurationResponse {
    
    private String issuerUri;
    private String clientId;
    // Note: clientSecret is never exposed in response
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String jwksUri;
    private Set<String> scopes;
    private Map<String, String> additionalParameters;
}
