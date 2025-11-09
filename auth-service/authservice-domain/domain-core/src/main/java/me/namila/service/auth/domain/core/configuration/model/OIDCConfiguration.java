package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * OIDC configuration value object containing OAuth2/OIDC provider settings.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCConfiguration {
    
    private String issuerUri;
    private String clientId;
    private String clientSecret; // Should be encrypted
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String jwksUri;
    
    @Builder.Default
    private Set<String> scopes = new HashSet<>();
    
    @Builder.Default
    private Map<String, String> additionalParameters = new HashMap<>();
}

