package me.namila.service.auth.domain.application.port.auth;

import me.namila.service.auth.domain.application.auth.dto.request.OIDCInitiateRequest;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;

import java.util.Map;

public interface OIDCIdentityProviderPort {

    String generateAuthorizationUrl(OIDCProviderConfigAggregate providerConfig, OIDCInitiateRequest request);

    Map<String, Object> exchangeAuthorizationCode(OIDCProviderConfigAggregate providerConfig, String code,
            String redirectUri);

    Map<String, Object> getUserInfo(OIDCProviderConfigAggregate providerConfig, String accessToken);
}
