package me.namila.service.auth.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.configuration.dto.request.CreateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.UpdateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigDetailResponse;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigResponse;
import me.namila.service.auth.domain.application.configuration.service.OIDCProviderConfigApplicationService;
import me.namila.service.auth.domain.application.dto.response.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for OIDC Provider Configuration management.
 * Provides endpoints for CRUD operations on OIDC provider configurations.
 */
@RestController
@RequestMapping("/api/v1/oidc-providers")
@RequiredArgsConstructor
@Tag(name = "OIDC Provider Configuration", description = "APIs for managing OIDC provider configurations")
public class OIDCProviderController {
    
    private final OIDCProviderConfigApplicationService configService;
    
    /**
     * Create a new OIDC provider configuration.
     */
    @PostMapping
    @Operation(
        summary = "Create OIDC provider configuration",
        description = "Creates a new OIDC provider configuration with authentication and provisioning settings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Configuration created successfully",
            content = @Content(schema = @Schema(implementation = OIDCProviderConfigResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Provider name already exists")
    })
    public ResponseEntity<OIDCProviderConfigResponse> createConfig(
        @Valid @RequestBody CreateOIDCProviderConfigRequest request) {
        OIDCProviderConfigResponse response = configService.createConfig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all OIDC provider configurations (paginated).
     */
    @GetMapping
    @Operation(
        summary = "List OIDC provider configurations",
        description = "Retrieves a paginated list of all OIDC provider configurations"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configurations retrieved successfully",
            content = @Content(schema = @Schema(implementation = PagedResponse.class))
        )
    })
    public ResponseEntity<PagedResponse<OIDCProviderConfigResponse>> getAllConfigs(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Sort field and direction", example = "createdAt,desc")
        @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        PagedResponse<OIDCProviderConfigResponse> response = configService.getAllConfigs(pageable);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get enabled OIDC provider configurations.
     */
    @GetMapping("/enabled")
    @Operation(
        summary = "List enabled OIDC providers",
        description = "Retrieves all enabled OIDC provider configurations"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Enabled configurations retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    public ResponseEntity<List<OIDCProviderConfigResponse>> getEnabledConfigs() {
        List<OIDCProviderConfigResponse> response = configService.getEnabledConfigs();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get OIDC provider configuration by ID.
     */
    @GetMapping("/{providerId}")
    @Operation(
        summary = "Get OIDC provider configuration",
        description = "Retrieves detailed information about a specific OIDC provider configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration found",
            content = @Content(schema = @Schema(implementation = OIDCProviderConfigDetailResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<OIDCProviderConfigDetailResponse> getConfigById(
        @Parameter(description = "Provider ID", required = true)
        @PathVariable UUID providerId) {
        OIDCProviderConfigDetailResponse response = configService.getConfigById(providerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get OIDC provider configuration by name.
     */
    @GetMapping("/by-name/{providerName}")
    @Operation(
        summary = "Get OIDC provider by name",
        description = "Retrieves detailed information about a specific OIDC provider configuration by name"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration found",
            content = @Content(schema = @Schema(implementation = OIDCProviderConfigDetailResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<OIDCProviderConfigDetailResponse> getConfigByName(
        @Parameter(description = "Provider name", required = true)
        @PathVariable String providerName) {
        OIDCProviderConfigDetailResponse response = configService.getConfigByName(providerName);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update OIDC provider configuration.
     */
    @PutMapping("/{providerId}")
    @Operation(
        summary = "Update OIDC provider configuration",
        description = "Updates an existing OIDC provider configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration updated successfully",
            content = @Content(schema = @Schema(implementation = OIDCProviderConfigResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<OIDCProviderConfigResponse> updateConfig(
        @Parameter(description = "Provider ID", required = true)
        @PathVariable UUID providerId,
        @Valid @RequestBody UpdateOIDCProviderConfigRequest request) {
        OIDCProviderConfigResponse response = configService.updateConfig(providerId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete OIDC provider configuration.
     */
    @DeleteMapping("/{providerId}")
    @Operation(
        summary = "Delete OIDC provider configuration",
        description = "Deletes an existing OIDC provider configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<Void> deleteConfig(
        @Parameter(description = "Provider ID", required = true)
        @PathVariable UUID providerId) {
        configService.deleteConfig(providerId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Enable OIDC provider configuration.
     */
    @PatchMapping("/{providerId}/enable")
    @Operation(
        summary = "Enable OIDC provider",
        description = "Enables an OIDC provider configuration for use in authentication"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration enabled successfully",
            content = @Content(schema = @Schema(implementation = OIDCProviderConfigResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<OIDCProviderConfigResponse> enableConfig(
        @Parameter(description = "Provider ID", required = true)
        @PathVariable UUID providerId) {
        OIDCProviderConfigResponse response = configService.enableConfig(providerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Disable OIDC provider configuration.
     */
    @PatchMapping("/{providerId}/disable")
    @Operation(
        summary = "Disable OIDC provider",
        description = "Disables an OIDC provider configuration, preventing its use in authentication"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Configuration disabled successfully",
            content = @Content(schema = @Schema(implementation = OIDCProviderConfigResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<OIDCProviderConfigResponse> disableConfig(
        @Parameter(description = "Provider ID", required = true)
        @PathVariable UUID providerId) {
        OIDCProviderConfigResponse response = configService.disableConfig(providerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test connection to OIDC provider.
     */
    @PostMapping("/{providerId}/test")
    @Operation(
        summary = "Test OIDC provider connection",
        description = "Tests the connection to an OIDC provider by validating configuration and connectivity"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Connection test successful"
        ),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "503", description = "Connection test failed")
    })
    public ResponseEntity<Map<String, Object>> testConnection(
        @Parameter(description = "Provider ID", required = true)
        @PathVariable UUID providerId) {
        boolean success = configService.testConnection(providerId);
        
        Map<String, Object> response = Map.of(
            "success", success,
            "message", success ? "Connection test successful" : "Connection test failed"
        );
        
        return ResponseEntity.ok(response);
    }
}
