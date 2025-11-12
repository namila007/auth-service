package me.namila.service.auth.data.identity.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.identity.mapper.UserEntityMapper;
import me.namila.service.auth.data.identity.repository.UserJpaRepository;
import me.namila.service.auth.domain.application.port.identity.UserRepositoryPort;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository adapter for User aggregate root.
 * Implements UserRepositoryPort using JPA.
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;
    
    @Override
    public UserAggregate save(UserAggregate user) {
        var entity = mapper.toEntity(user);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<UserAggregate> findById(UserId userId) {
        return jpaRepository.findById(userId.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<UserAggregate> findByUsername(UsernameValue username) {
        return jpaRepository.findByUsername(username.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<UserAggregate> findByEmail(EmailValue email) {
        return jpaRepository.findByEmail(email.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByUsername(UsernameValue username) {
        return jpaRepository.existsByUsername(username.getValue());
    }
    
    @Override
    public boolean existsByEmail(EmailValue email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
    
    @Override
    public List<UserAggregate> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UserId userId) {
        jpaRepository.deleteById(userId.getValue());
    }
    
    @Override
    public List<UserAggregate> findByStatus(UserStatus status) {
        return jpaRepository.findAll().stream()
            .filter(entity -> entity.getStatus().equals(status.name()))
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}

