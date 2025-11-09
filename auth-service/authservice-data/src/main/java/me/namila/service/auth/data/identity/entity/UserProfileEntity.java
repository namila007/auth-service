package me.namila.service.auth.data.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for UserProfile.
 */
@Entity
@Table(name = "user_profiles", schema = "identity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {
    
    @Id
    @Column(name = "profile_id")
    private UUID profileId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;
    
    @Column(name = "first_name", length = 255)
    private String firstName;
    
    @Column(name = "last_name", length = 255)
    private String lastName;
    
    @Column(name = "display_name", length = 255)
    private String displayName;
    
    @Column(name = "attributes", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> attributes;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

