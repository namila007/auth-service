package me.namila.service.auth.domain.application.auth.dto.response;

import lombok.Builder;
import me.namila.service.auth.domain.application.identity.dto.response.UserResponse;

@Builder
public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresIn,
        UserResponse user) {
}
