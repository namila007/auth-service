package me.namila.service.auth.domain.application.auth.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.identity.dto.request.CreateUserRequest;
import me.namila.service.auth.domain.application.identity.dto.request.UserProfileRequest;
import me.namila.service.auth.domain.application.identity.service.UserApplicationService;
import me.namila.service.auth.domain.application.port.identity.FederatedIdentityRepositoryPort;
import me.namila.service.auth.domain.application.port.identity.UserRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.service.IdentityFederationService;

@Service
@RequiredArgsConstructor
@Transactional
public class JITProvisioningService {

    private final UserApplicationService userApplicationService;
    private final UserRepositoryPort userRepositoryPort;
    private final FederatedIdentityRepositoryPort federatedIdentityRepositoryPort;
    private final IdentityFederationService identityFederationService;

    public UserAggregate provisionUser(OIDCProviderConfigAggregate provider, Map<String, Object> userInfo) {
        // 1. Extract subject ID
        String subjectAttribute = provider.getAttributeMapping().getSubjectAttribute();
        String subjectId = (String) userInfo.get(subjectAttribute);

        if (subjectId == null) {
            throw new IllegalArgumentException(
                    "Subject ID not found in user info using attribute: " + subjectAttribute);
        }

        // 2. Check if federated identity exists
        Optional<FederatedIdentityEntity> existingIdentity = federatedIdentityRepositoryPort
                .findByProviderIdAndSubjectId(provider.getId(), subjectId);

        UserAggregate user;

        if (existingIdentity.isPresent()) {
            // 3a. Update existing user
            FederatedIdentityEntity identity = existingIdentity.get();
            user = userRepositoryPort.findById(identity.getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found for existing identity"));

            if (Boolean.TRUE.equals(provider.getJitProvisioning().getUpdateUsers())) {
                updateUserAttributes(user, userInfo, provider);
            }

            // Update last synced
            identity.updateLastSyncedAt();
            federatedIdentityRepositoryPort.save(identity);

        } else {
            // 3b. Create new user
            if (!Boolean.TRUE.equals(provider.getJitProvisioning().getCreateUsers())) {
                throw new IllegalStateException("User provisioning is disabled for this provider");
            }

            user = createNewUser(userInfo, provider);

            // Link federated identity
            FederatedIdentityEntity identity = identityFederationService.createFederatedIdentity(
                    user, provider.getId(), subjectId, provider.getConfiguration().getIssuerUri());

            federatedIdentityRepositoryPort.save(identity);

            // Add to user aggregate (in memory)
            user.addFederatedIdentity(identity);
        }

        // 4. Sync roles (TODO: Implement role sync)

        return user;
    }

    private UserAggregate createNewUser(Map<String, Object> userInfo, OIDCProviderConfigAggregate provider) {
        String emailAttr = provider.getAttributeMapping().getEmailAttribute();
        String nameAttr = provider.getAttributeMapping().getNameAttribute();

        String email = (String) userInfo.get(emailAttr);
        String name = (String) userInfo.get(nameAttr);

        // Fallback for username if not present/mapped
        String username = email;

        UserProfileRequest profileRequest = UserProfileRequest.builder()
                .firstName(name) // Simple mapping for now
                .lastName("")
                .displayName(name)
                .build();

        CreateUserRequest createRequest = CreateUserRequest.builder()
                .username(username)
                .email(email)
                .profile(profileRequest)
                .build();

        var response = userApplicationService.createUser(createRequest);

        return userRepositoryPort
                .findById(me.namila.service.auth.domain.core.identity.model.id.UserId.of(response.getUserId()))
                .orElseThrow();
    }

    private void updateUserAttributes(UserAggregate user, Map<String, Object> userInfo,
            OIDCProviderConfigAggregate provider) {
        // TODO: Implement attribute update logic based on mapping
    }
}
