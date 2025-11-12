package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.FederatedIdentityJpaEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for FederatedIdentity Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FederatedIdentityEntityMapper {
    
    @Mapping(target = "id", source = "federatedIdentityId", qualifiedByName = "uuidToFederatedIdentityId")
    @Mapping(target = "userId", source = "user.userId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "providerId", source = "providerId")
    @Mapping(target = "subjectId", source = "subjectId")
    @Mapping(target = "issuer", source = "issuer")
    @Mapping(target = "linkedAt", source = "linkedAt")
    @Mapping(target = "lastSyncedAt", source = "lastSyncedAt")
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity toDomain(FederatedIdentityJpaEntity entity);
    
    @Mapping(target = "federatedIdentityId", source = "id.value")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "providerId", source = "providerId")
    @Mapping(target = "subjectId", source = "subjectId")
    @Mapping(target = "issuer", source = "issuer")
    @Mapping(target = "linkedAt", source = "linkedAt")
    @Mapping(target = "lastSyncedAt", source = "lastSyncedAt")
    @Mapping(target = "metadata", source = "metadata")
    FederatedIdentityJpaEntity toEntity(me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity domain);
    
    @Named("uuidToFederatedIdentityId")
    default FederatedIdentityId uuidToFederatedIdentityId(java.util.UUID uuid) {
        return uuid != null ? FederatedIdentityId.of(uuid) : null;
    }
    
    @Named("uuidToUserId")
    default UserId uuidToUserId(java.util.UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }
}

