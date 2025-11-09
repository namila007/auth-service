package me.namila.service.auth.domain.core.identity.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseAggregate;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User aggregate root - the main entity for user management.
 * This is a rich domain model with business logic.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserAggregate extends BaseAggregate<UserId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private UserId id = UserId.generate();
    
    private UsernameValue username;
    private EmailValue email;
    
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;
    
    private UserProfileEntity profile;
    
    @Builder.Default
    private List<FederatedIdentityEntity> federatedIdentities = new ArrayList<>();
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    @Builder.Default
    private Long version = 0L;
    
    public void activate() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new IllegalStateException("Cannot activate a suspended user");
        }
        this.status = UserStatus.ACTIVE;
        markAsUpdated();
    }
    
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        markAsUpdated();
    }
    
    public void lock() {
        this.status = UserStatus.LOCKED;
        markAsUpdated();
    }
    
    public void unlock() {
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
            markAsUpdated();
        }
    }
    
    public void addFederatedIdentity(FederatedIdentityEntity federatedIdentity) {
        if (federatedIdentity == null) {
            throw new IllegalArgumentException("Federated identity cannot be null");
        }
        // Check for duplicate provider + subject combination
        boolean exists = federatedIdentities.stream()
            .anyMatch(fi -> fi.getProviderId().equals(federatedIdentity.getProviderId()) &&
                           fi.getSubjectId().equals(federatedIdentity.getSubjectId()));
        if (exists) {
            throw new IllegalStateException(
                "Federated identity with provider " + federatedIdentity.getProviderId() +
                " and subject " + federatedIdentity.getSubjectId() + " already exists"
            );
        }
        federatedIdentity.linkToUser(this.id);
        this.federatedIdentities.add(federatedIdentity);
        markAsUpdated();
    }
    
    public void updateEmail(EmailValue newEmail) {
        if (!this.email.equals(newEmail)) {
            this.email = newEmail;
            markAsUpdated();
        }
    }
    
    public void updateProfile(UserProfileEntity profile) {
        this.profile = profile;
        markAsUpdated();
    }
    
    public List<FederatedIdentityEntity> getFederatedIdentities() {
        return federatedIdentities != null ? new ArrayList<>(federatedIdentities) : new ArrayList<>();
    }
    
    public void setFederatedIdentities(List<FederatedIdentityEntity> federatedIdentities) {
        this.federatedIdentities = federatedIdentities != null ? new ArrayList<>(federatedIdentities) : new ArrayList<>();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    public void incrementVersion() {
        this.version = (this.version == null ? 0L : this.version) + 1L;
    }
}

