package me.namila.service.auth.domain.application.auth.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OIDCCallbackRequest(
        @NotNull(message = "Provider ID is required") UUID providerId,

        @NotBlank(message = "Authorization code is required") String code,

        @NotBlank(message = "State is required") String state,

        String codeVerifier) {
}
