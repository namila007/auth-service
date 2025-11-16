package me.namila.service.auth.domain.application.configuration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for custom attribute mapping.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMappingRequest {
    
    private String externalAttribute;
    private String internalAttribute;
    private String transformationRule;
}
