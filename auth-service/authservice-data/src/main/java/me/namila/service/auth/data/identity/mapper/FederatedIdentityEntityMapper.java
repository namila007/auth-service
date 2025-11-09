package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.FederatedIdentity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for FederatedIdentity Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FederatedIdentityEntityMapper {
    
    FederatedIdentity toDomain(FederatedIdentityEntity entity);
    
    FederatedIdentityEntity toEntity(FederatedIdentity domain);
}

