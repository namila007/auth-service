package me.namila.service.auth.domain.core.governance.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseEntity;
import me.namila.service.auth.domain.core.governance.model.id.AccessCertificationId;
import me.namila.service.auth.domain.core.governance.valueobject.CertificationStatus;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;

import java.time.Instant;

/**
 * AccessCertification entity representing an access certification review.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AccessCertificationEntity extends BaseEntity<AccessCertificationId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private AccessCertificationId id = AccessCertificationId.generate();
    
    private UserId userId;
    private RoleId roleId;
    
    @Builder.Default
    private Instant certificationDate = Instant.now();
    
    private UserId certifiedBy;
    private CertificationStatus status;
    private Instant nextReviewDate;
    private String notes;
}

