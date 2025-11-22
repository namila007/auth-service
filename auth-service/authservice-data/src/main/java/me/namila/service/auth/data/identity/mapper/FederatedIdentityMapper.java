package me.namila.service.auth.data.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import me.namila.service.auth.data.identity.entity.FederatedIdentityJpaEntity;
import me.namila.service.auth.domain.core.identity.model.entity.FederatedIdentityEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FederatedIdentityMapper {

    @Mapping(target = "id", expression = "java(me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId.of(entity.getFederatedIdentityId()))")
    @Mapping(target = "userId", expression = "java(me.namila.service.auth.domain.core.identity.model.id.UserId.of(entity.getUserId()))")
    @Mapping(target = "providerId", expression = "java(me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId.of(entity.getProviderId()))")
    @Mapping(target = "linkedAt", expression = "java(java.time.LocalDateTime.ofInstant(entity.getLinkedAt(), java.time.ZoneOffset.UTC))")
    @Mapping(target = "lastSyncedAt", expression = "java(entity.getLastSyncedAt() != null ? java.time.LocalDateTime.ofInstant(entity.getLastSyncedAt(), java.time.ZoneOffset.UTC) : null)")
    FederatedIdentityEntity toDomain(FederatedIdentityJpaEntity entity);

    @Mapping(target = "federatedIdentityId", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "providerId", source = "providerId.value")
    @Mapping(target = "linkedAt", expression = "java(domain.getLinkedAt().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "lastSyncedAt", expression = "java(domain.getLastSyncedAt() != null ? domain.getLastSyncedAt().toInstant(java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "user", ignore = true)
    FederatedIdentityJpaEntity toEntity(FederatedIdentityEntity domain);
}
