package me.namila.service.auth.domain.application.auth.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCCallbackRequest;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCInitiateRequest;
import me.namila.service.auth.domain.application.auth.dto.response.AuthenticationResponse;
import me.namila.service.auth.domain.application.configuration.service.OIDCProviderConfigApplicationService;
import me.namila.service.auth.domain.application.identity.mapper.UserDtoMapper;
import me.namila.service.auth.domain.application.port.auth.OIDCIdentityProviderPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;

@Service
@RequiredArgsConstructor
@Transactional
public class OIDCAuthenticationService {

    private final OIDCProviderConfigApplicationService providerConfigService;
    private final OIDCIdentityProviderPort identityProviderPort;
    private final JITProvisioningService jitProvisioningService;
    private final UserDtoMapper userDtoMapper;

    public String initiateAuthentication(OIDCInitiateRequest request) {
        OIDCProviderConfigAggregate provider = providerConfigService.getProviderAggregate(request.providerId());

        if (!Boolean.TRUE.equals(provider.getEnabled())) {
            throw new IllegalStateException("Provider is disabled: " + provider.getProviderName());
        }

        return identityProviderPort.generateAuthorizationUrl(provider, request);
    }

    public AuthenticationResponse handleCallback(OIDCCallbackRequest request) {
        OIDCProviderConfigAggregate provider = providerConfigService.getProviderAggregate(request.providerId());

        // 1. Exchange code for tokens
        // Note: redirectUri is needed for exchange, usually passed in request or
        // configured
        String redirectUri = null; // Should be passed in request if dynamic
        Map<String, Object> tokenResponse = identityProviderPort.exchangeAuthorizationCode(provider, request.code(),
                redirectUri);
        String accessToken = (String) tokenResponse.get("access_token");

        // 2. Get User Info
        Map<String, Object> userInfo = identityProviderPort.getUserInfo(provider, accessToken);

        // 3. JIT Provisioning
        UserAggregate user = jitProvisioningService.provisionUser(provider, userInfo);

        // 4. Generate Internal Tokens (TODO: Implement JWT generation)
        String internalAccessToken = "mock-access-token";
        String internalRefreshToken = "mock-refresh-token";

        return AuthenticationResponse.builder()
                .accessToken(internalAccessToken)
                .refreshToken(internalRefreshToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .user(userDtoMapper.toResponse(user))
                .build();
    }
}
