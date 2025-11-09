package me.namila.service.auth.domain.core.identity.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * UserProfile identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfileId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private UserProfileId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserProfile ID cannot be null");
        }
        this.value = value;
    }
    
    public static UserProfileId of(String id) {
        return new UserProfileId(UUID.fromString(id));
    }
    
    public static UserProfileId of(UUID id) {
        return new UserProfileId(id);
    }
    
    public static UserProfileId generate() {
        return new UserProfileId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserProfile ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}

