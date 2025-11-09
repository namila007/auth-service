package me.namila.service.auth.domain.core.identity.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * User identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.value = value;
    }
    
    /**
     * Create UserId from string representation.
     * 
     * @param id the UUID string
     * @return UserId instance
     */
    public static UserId of(String id) {
        return new UserId(UUID.fromString(id));
    }
    
    /**
     * Create UserId from UUID.
     * 
     * @param id the UUID
     * @return UserId instance
     */
    public static UserId of(UUID id) {
        return new UserId(id);
    }
    
    /**
     * Generate a new UserId using UUIDv7 (time-ordered).
     * 
     * @return new UserId instance
     */
    public static UserId generate() {
        return new UserId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}

