package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * Request DTO for OIDC configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCConfigurationRequest {
    
    @NotBlank(message = "Issuer URI is required")
    private String issuerUri;
    
    @NotBlank(message = "Client ID is required")
    private String clientId;
    
    @NotBlank(message = "Client secret is required")
    private String clientSecret;
    
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String jwksUri;
    
    @NotNull(message = "Scopes are required")
    private Set<String> scopes;
    
    private Map<String, String> additionalParameters;
}
