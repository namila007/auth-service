package me.namila.service.auth.domain.application.identity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for user profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    
    private String firstName;
    
    private String lastName;
    
    private String displayName;
    
    private Map<String, Object> attributes;
}

