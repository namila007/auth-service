package me.namila.service.auth.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCCallbackRequest;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCInitiateRequest;
import me.namila.service.auth.domain.application.auth.dto.response.AuthenticationResponse;
import me.namila.service.auth.domain.application.auth.service.OIDCAuthenticationService;
import me.namila.service.auth.common.api.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthenticationController {

    private final OIDCAuthenticationService authenticationService;

    @PostMapping("/oidc/initiate")
    @Operation(summary = "Initiate OIDC authentication", description = "Generates the authorization URL for the specified provider")
    public ResponseEntity<ApiResponse<String>> initiateOIDC(@Valid @RequestBody OIDCInitiateRequest request) {
        String authorizationUrl = authenticationService.initiateAuthentication(request);
        return ResponseEntity.ok(ApiResponse.success(authorizationUrl));
    }

    @PostMapping("/oidc/callback")
    @Operation(summary = "Handle OIDC callback", description = "Exchanges authorization code for tokens and performs JIT provisioning")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> handleOIDCCallback(
            @Valid @RequestBody OIDCCallbackRequest request) {
        AuthenticationResponse response = authenticationService.handleCallback(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
