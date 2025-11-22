package me.namila.service.auth.data.auth.adapter;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCInitiateRequest;
import me.namila.service.auth.domain.application.port.auth.OIDCIdentityProviderPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;

@Component
@RequiredArgsConstructor
public class OIDCIdentityProviderAdapter implements OIDCIdentityProviderPort {

    // We might need a dynamic way to register clients if we want to use Spring
    // Security's features fully
    // But for now, we can construct requests manually or use a helper

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateAuthorizationUrl(OIDCProviderConfigAggregate providerConfig, OIDCInitiateRequest request) {
        // Construct URL manually or use a builder
        String authorizationUri = providerConfig.getConfiguration().getAuthorizationUri();
        String clientId = providerConfig.getConfiguration().getClientId();
        String redirectUri = request.redirectUri();
        String scope = String.join(" ", providerConfig.getConfiguration().getScopes());
        String state = request.state() != null ? request.state() : "state";

        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                authorizationUri, clientId, redirectUri, scope, state);
    }

    @Override
    public Map<String, Object> exchangeAuthorizationCode(OIDCProviderConfigAggregate providerConfig, String code,
            String redirectUri) {
        String tokenUri = providerConfig.getConfiguration().getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", redirectUri);
        map.add("client_id", providerConfig.getConfiguration().getClientId());
        map.add("client_secret", providerConfig.getConfiguration().getClientSecret()); // Should be decrypted

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        return response.getBody();
    }

    @Override
    public Map<String, Object> getUserInfo(OIDCProviderConfigAggregate providerConfig, String accessToken) {
        String userInfoUri = providerConfig.getConfiguration().getUserInfoUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, org.springframework.http.HttpMethod.GET,
                request, Map.class);

        return response.getBody();
    }
}
