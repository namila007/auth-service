package me.namila.service.auth.domain.application.identity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating an existing user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Email(message = "Email must be a valid email address")
    private String email;
    
    @Valid
    private UserProfileRequest profile;
    
    private Map<String, Object> metadata;
}

