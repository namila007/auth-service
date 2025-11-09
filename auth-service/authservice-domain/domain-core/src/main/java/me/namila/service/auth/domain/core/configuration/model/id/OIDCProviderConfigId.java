package me.namila.service.auth.domain.core.configuration.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * OIDCProviderConfig identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OIDCProviderConfigId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private OIDCProviderConfigId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OIDCProviderConfig ID cannot be null");
        }
        this.value = value;
    }
    
    public static OIDCProviderConfigId of(String id) {
        return new OIDCProviderConfigId(UUID.fromString(id));
    }
    
    public static OIDCProviderConfigId of(UUID id) {
        return new OIDCProviderConfigId(id);
    }
    
    public static OIDCProviderConfigId generate() {
        return new OIDCProviderConfigId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OIDCProviderConfig ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}

