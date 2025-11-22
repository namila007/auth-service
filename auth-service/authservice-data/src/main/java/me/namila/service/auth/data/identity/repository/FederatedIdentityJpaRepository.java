package me.namila.service.auth.data.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.namila.service.auth.data.identity.entity.FederatedIdentityJpaEntity;

@Repository
public interface FederatedIdentityJpaRepository extends JpaRepository<FederatedIdentityJpaEntity, UUID> {

    Optional<FederatedIdentityJpaEntity> findByProviderIdAndSubjectId(UUID providerId, String subjectId);
}
