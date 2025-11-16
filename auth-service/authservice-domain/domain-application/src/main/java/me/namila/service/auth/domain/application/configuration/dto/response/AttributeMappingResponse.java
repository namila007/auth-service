package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for custom attribute mapping.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMappingResponse {
    
    private String externalAttribute;
    private String internalAttribute;
    private String transformationRule;
}
