package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for attribute mapping configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMappingConfigResponse {
    
    private String subjectAttribute;
    private String emailAttribute;
    private String nameAttribute;
    private String groupsAttribute;
    private List<AttributeMappingResponse> customMappings;
}
