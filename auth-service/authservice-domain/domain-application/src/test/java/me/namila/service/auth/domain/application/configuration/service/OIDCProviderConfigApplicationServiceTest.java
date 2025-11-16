package me.namila.service.auth.domain.application.configuration.service;

import me.namila.service.auth.domain.application.configuration.dto.request.CreateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.OIDCConfigurationRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.UpdateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigDetailResponse;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigResponse;
import me.namila.service.auth.domain.application.configuration.mapper.OIDCProviderConfigDtoMapper;
import me.namila.service.auth.domain.application.dto.response.PagedResponse;
import me.namila.service.auth.domain.application.port.configuration.OIDCProviderConfigRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCConfiguration;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
import me.namila.service.auth.domain.core.exception.DuplicateEntityException;
import me.namila.service.auth.domain.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OIDCProviderConfigApplicationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OIDCProviderConfigApplicationService Unit Tests")
class OIDCProviderConfigApplicationServiceTest {
    
    @Mock
    private OIDCProviderConfigRepositoryPort configRepository;
    
    @Mock
    private OIDCProviderConfigDtoMapper mapper;
    
    @InjectMocks
    private OIDCProviderConfigApplicationService service;
    
    // ==================== Test Data Builders ====================
    
    private CreateOIDCProviderConfigRequest createRequest() {
        return CreateOIDCProviderConfigRequest.builder()
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType("OIDC")
            .enabled(true)
            .configuration(OIDCConfigurationRequest.builder()
                .issuerUri("https://test.auth.com")
                .clientId("test-client")
                .clientSecret("test-secret")
                .build())
            .build();
    }
    
    private UpdateOIDCProviderConfigRequest updateRequest() {
        return UpdateOIDCProviderConfigRequest.builder()
            .displayName("Updated Provider")
            .enabled(false)
            .build();
    }
    
    private OIDCProviderConfigAggregate createAggregate() {
        return OIDCProviderConfigAggregate.builder()
            .id(OIDCProviderConfigId.of(UUID.randomUUID()))
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType(ProviderType.OIDC)
            .enabled(true)
            .configuration(OIDCConfiguration.builder()
                .issuerUri("https://test.auth.com")
                .clientId("test-client")
                .clientSecret("test-secret")
                .build())
            .build();
    }
    
    private OIDCProviderConfigResponse createResponse() {
        return new OIDCProviderConfigResponse(
            UUID.randomUUID(),
            "test-provider",
            "Test Provider",
            "OIDC",
            true,
            Instant.now(),
            Instant.now(),
            null
        );
    }
    
    private OIDCProviderConfigDetailResponse createDetailResponse() {
        return OIDCProviderConfigDetailResponse.builder()
            .providerId(UUID.randomUUID())
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType("OIDC")
            .enabled(true)
            .createdAt(Instant.now())
            .lastModifiedAt(Instant.now())
            .build();
    }
    
    // ==================== createConfig Tests ====================
    
    @Test
    @DisplayName("Should create new OIDC provider configuration successfully")
    void shouldCreateConfigSuccessfully() {
        // Given
        CreateOIDCProviderConfigRequest request = createRequest();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        OIDCProviderConfigResponse response = createResponse();
        
        when(configRepository.existsByProviderName(request.getProviderName())).thenReturn(false);
        when(mapper.toDomain(request)).thenReturn(aggregate);
        when(configRepository.save(any(OIDCProviderConfigAggregate.class))).thenReturn(aggregate);
        when(mapper.toResponse(aggregate)).thenReturn(response);
        
        // When
        OIDCProviderConfigResponse result = service.createConfig(request);
        
        // Then
        assertNotNull(result);
        assertEquals(response.getProviderName(), result.getProviderName());
        verify(configRepository).existsByProviderName(request.getProviderName());
        verify(mapper).toDomain(request);
        verify(configRepository).save(any(OIDCProviderConfigAggregate.class));
        verify(mapper).toResponse(aggregate);
    }
    
    @Test
    @DisplayName("Should throw DuplicateEntityException when provider name already exists")
    void shouldThrowDuplicateEntityExceptionWhenProviderNameExists() {
        // Given
        CreateOIDCProviderConfigRequest request = createRequest();
        
        when(configRepository.existsByProviderName(request.getProviderName())).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateEntityException.class, () -> service.createConfig(request));
        verify(configRepository).existsByProviderName(request.getProviderName());
        verify(mapper, never()).toDomain(any());
        verify(configRepository, never()).save(any());
    }
    
    // ==================== getConfigById Tests ====================
    
    @Test
    @DisplayName("Should get configuration by ID successfully")
    void shouldGetConfigByIdSuccessfully() {
        // Given
        UUID providerId = UUID.randomUUID();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        OIDCProviderConfigDetailResponse response = createDetailResponse();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.of(aggregate));
        when(mapper.toDetailResponse(aggregate)).thenReturn(response);
        
        // When
        OIDCProviderConfigDetailResponse result = service.getConfigById(providerId);
        
        // Then
        assertNotNull(result);
        assertEquals(response.getProviderName(), result.getProviderName());
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(mapper).toDetailResponse(aggregate);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when configuration not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenConfigNotFoundById() {
        // Given
        UUID providerId = UUID.randomUUID();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.getConfigById(providerId));
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(mapper, never()).toDetailResponse(any());
    }
    
    // ==================== getConfigByName Tests ====================
    
    @Test
    @DisplayName("Should get configuration by name successfully")
    void shouldGetConfigByNameSuccessfully() {
        // Given
        String providerName = "test-provider";
        OIDCProviderConfigAggregate aggregate = createAggregate();
        OIDCProviderConfigDetailResponse response = createDetailResponse();
        
        when(configRepository.findByProviderName(providerName)).thenReturn(Optional.of(aggregate));
        when(mapper.toDetailResponse(aggregate)).thenReturn(response);
        
        // When
        OIDCProviderConfigDetailResponse result = service.getConfigByName(providerName);
        
        // Then
        assertNotNull(result);
        assertEquals(response.getProviderName(), result.getProviderName());
        verify(configRepository).findByProviderName(providerName);
        verify(mapper).toDetailResponse(aggregate);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when configuration not found by name")
    void shouldThrowResourceNotFoundExceptionWhenConfigNotFoundByName() {
        // Given
        String providerName = "non-existent-provider";
        
        when(configRepository.findByProviderName(providerName)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.getConfigByName(providerName));
        verify(configRepository).findByProviderName(providerName);
        verify(mapper, never()).toDetailResponse(any());
    }
    
    // ==================== getAllConfigs Tests ====================
    
    @Test
    @DisplayName("Should get all configurations with pagination")
    void shouldGetAllConfigsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<OIDCProviderConfigAggregate> aggregates = Arrays.asList(createAggregate(), createAggregate());
        Page<OIDCProviderConfigAggregate> page = new PageImpl<>(aggregates, pageable, aggregates.size());
        OIDCProviderConfigResponse response = createResponse();
        
        when(configRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(any(OIDCProviderConfigAggregate.class))).thenReturn(response);
        
        // When
        PagedResponse<OIDCProviderConfigResponse> result = service.getAllConfigs(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(configRepository).findAll(pageable);
        verify(mapper, times(2)).toResponse(any(OIDCProviderConfigAggregate.class));
    }
    
    @Test
    @DisplayName("Should return empty page when no configurations exist")
    void shouldReturnEmptyPageWhenNoConfigurationsExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<OIDCProviderConfigAggregate> emptyPage = Page.empty(pageable);
        
        when(configRepository.findAll(pageable)).thenReturn(emptyPage);
        
        // When
        PagedResponse<OIDCProviderConfigResponse> result = service.getAllConfigs(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        verify(configRepository).findAll(pageable);
        verify(mapper, never()).toResponse(any());
    }
    
    // ==================== getEnabledConfigs Tests ====================
    
    @Test
    @DisplayName("Should get all enabled configurations")
    void shouldGetEnabledConfigs() {
        // Given
        List<OIDCProviderConfigAggregate> aggregates = Arrays.asList(createAggregate(), createAggregate());
        OIDCProviderConfigResponse response = createResponse();
        
        when(configRepository.findEnabledConfigs()).thenReturn(aggregates);
        when(mapper.toResponse(any(OIDCProviderConfigAggregate.class))).thenReturn(response);
        
        // When
        List<OIDCProviderConfigResponse> result = service.getEnabledConfigs();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(configRepository).findEnabledConfigs();
        verify(mapper, times(2)).toResponse(any(OIDCProviderConfigAggregate.class));
    }
    
    @Test
    @DisplayName("Should return empty list when no enabled configurations exist")
    void shouldReturnEmptyListWhenNoEnabledConfigurationsExist() {
        // Given
        when(configRepository.findEnabledConfigs()).thenReturn(List.of());
        
        // When
        List<OIDCProviderConfigResponse> result = service.getEnabledConfigs();
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(configRepository).findEnabledConfigs();
        verify(mapper, never()).toResponse(any());
    }
    
    // ==================== updateConfig Tests ====================
    
    @Test
    @DisplayName("Should update configuration successfully")
    void shouldUpdateConfigSuccessfully() {
        // Given
        UUID providerId = UUID.randomUUID();
        UpdateOIDCProviderConfigRequest request = updateRequest();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        OIDCProviderConfigResponse response = createResponse();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.of(aggregate));
        when(configRepository.save(aggregate)).thenReturn(aggregate);
        when(mapper.toResponse(aggregate)).thenReturn(response);
        
        // When
        OIDCProviderConfigResponse result = service.updateConfig(providerId, request);
        
        // Then
        assertNotNull(result);
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(mapper).updateDomainFromRequest(request, aggregate);
        verify(configRepository).save(aggregate);
        verify(mapper).toResponse(aggregate);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent configuration")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentConfig() {
        // Given
        UUID providerId = UUID.randomUUID();
        UpdateOIDCProviderConfigRequest request = updateRequest();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.updateConfig(providerId, request));
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(mapper, never()).updateDomainFromRequest(any(), any());
        verify(configRepository, never()).save(any());
    }
    
    // ==================== deleteConfig Tests ====================
    
    @Test
    @DisplayName("Should delete configuration successfully")
    void shouldDeleteConfigSuccessfully() {
        // Given
        UUID providerId = UUID.randomUUID();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.of(aggregate));
        
        // When
        service.deleteConfig(providerId);
        
        // Then
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(configRepository).deleteById(OIDCProviderConfigId.of(providerId));
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent configuration")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentConfig() {
        // Given
        UUID providerId = UUID.randomUUID();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.deleteConfig(providerId));
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(configRepository, never()).deleteById(any());
    }
    
    // ==================== enableConfig Tests ====================
    
    @Test
    @DisplayName("Should enable configuration successfully")
    void shouldEnableConfigSuccessfully() {
        // Given
        UUID providerId = UUID.randomUUID();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        OIDCProviderConfigResponse response = createResponse();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.of(aggregate));
        when(configRepository.save(aggregate)).thenReturn(aggregate);
        when(mapper.toResponse(aggregate)).thenReturn(response);
        
        // When
        OIDCProviderConfigResponse result = service.enableConfig(providerId);
        
        // Then
        assertNotNull(result);
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(configRepository).save(aggregate);
        verify(mapper).toResponse(aggregate);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when enabling non-existent configuration")
    void shouldThrowResourceNotFoundExceptionWhenEnablingNonExistentConfig() {
        // Given
        UUID providerId = UUID.randomUUID();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.enableConfig(providerId));
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(configRepository, never()).save(any());
    }
    
    // ==================== disableConfig Tests ====================
    
    @Test
    @DisplayName("Should disable configuration successfully")
    void shouldDisableConfigSuccessfully() {
        // Given
        UUID providerId = UUID.randomUUID();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        OIDCProviderConfigResponse response = createResponse();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.of(aggregate));
        when(configRepository.save(aggregate)).thenReturn(aggregate);
        when(mapper.toResponse(aggregate)).thenReturn(response);
        
        // When
        OIDCProviderConfigResponse result = service.disableConfig(providerId);
        
        // Then
        assertNotNull(result);
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(configRepository).save(aggregate);
        verify(mapper).toResponse(aggregate);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when disabling non-existent configuration")
    void shouldThrowResourceNotFoundExceptionWhenDisablingNonExistentConfig() {
        // Given
        UUID providerId = UUID.randomUUID();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.disableConfig(providerId));
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
        verify(configRepository, never()).save(any());
    }
    
    // ==================== testConnection Tests ====================
    
    @Test
    @DisplayName("Should test connection successfully")
    void shouldTestConnectionSuccessfully() {
        // Given
        UUID providerId = UUID.randomUUID();
        OIDCProviderConfigAggregate aggregate = createAggregate();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.of(aggregate));
        
        // When
        boolean result = service.testConnection(providerId);
        
        // Then
        assertTrue(result);
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when testing connection for non-existent configuration")
    void shouldThrowResourceNotFoundExceptionWhenTestingConnectionForNonExistentConfig() {
        // Given
        UUID providerId = UUID.randomUUID();
        
        when(configRepository.findById(OIDCProviderConfigId.of(providerId))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.testConnection(providerId));
        verify(configRepository).findById(OIDCProviderConfigId.of(providerId));
    }
}
