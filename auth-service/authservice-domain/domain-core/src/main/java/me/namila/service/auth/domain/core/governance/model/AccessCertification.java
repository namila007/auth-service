package me.namila.service.auth.domain.core.governance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.domain.core.governance.valueobject.CertificationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * AccessCertification entity representing an access certification review.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "certificationId")
public class AccessCertification {
    
    @Builder.Default
    private UUID certificationId = UUID.randomUUID();
    
    private UUID userId;
    private UUID roleId;
    
    @Builder.Default
    private Instant certificationDate = Instant.now();
    
    private UUID certifiedBy;
    private CertificationStatus status;
    private Instant nextReviewDate;
    private String notes;
}

