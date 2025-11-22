package me.namila.service.auth.domain.application.auth.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OIDCInitiateRequest(
        @NotNull(message = "Provider ID is required") UUID providerId,

        String redirectUri,

        String state,

        String nonce) {
}
