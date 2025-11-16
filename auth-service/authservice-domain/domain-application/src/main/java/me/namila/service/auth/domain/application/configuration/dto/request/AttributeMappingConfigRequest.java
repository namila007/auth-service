package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for attribute mapping configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMappingConfigRequest {
    
    private String subjectAttribute;
    private String emailAttribute;
    private String nameAttribute;
    private String groupsAttribute;
    
    @Valid
    private List<AttributeMappingRequest> customMappings;
}
