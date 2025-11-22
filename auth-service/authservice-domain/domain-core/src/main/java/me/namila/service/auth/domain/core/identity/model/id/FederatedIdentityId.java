package me.namila.service.auth.domain.core.identity.model.id;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

import me.namila.service.auth.common.domain.BaseId;

/**
 * Identifier for FederatedIdentity entity.
 */
public class FederatedIdentityId implements BaseId<UUID> {

    private UUID value;

    public FederatedIdentityId(UUID value) {
        this.value = value;
    }

    @Override
    public UUID getValue() {
        return value;
    }

    @Override
    public void setValue(UUID value) {
        this.value = value;
    }

    public static FederatedIdentityId of(UUID value) {
        return new FederatedIdentityId(value);
    }

    public static FederatedIdentityId of(String value) {
        return new FederatedIdentityId(UUID.fromString(value));
    }

    public static FederatedIdentityId generate() {
        return new FederatedIdentityId(UuidCreator.getTimeOrderedEpoch());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FederatedIdentityId that = (FederatedIdentityId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
