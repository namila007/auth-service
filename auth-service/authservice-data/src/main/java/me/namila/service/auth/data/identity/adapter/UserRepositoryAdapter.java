package me.namila.service.auth.data.identity.adapter;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.data.identity.mapper.UserEntityMapper;
import me.namila.service.auth.data.identity.repository.UserJpaRepository;
import me.namila.service.auth.domain.application.port.identity.UserRepositoryPort;
import me.namila.service.auth.domain.core.identity.model.User;
import me.namila.service.auth.domain.core.identity.valueobject.Email;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.Username;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<User> findById(UUID userId) {
        return jpaRepository.findById(userId)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByUsername(Username username) {
        return jpaRepository.findByUsername(username.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByUsername(Username username) {
        return jpaRepository.existsByUsername(username.getValue());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
    
    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID userId) {
        jpaRepository.deleteById(userId);
    }
    
    @Override
    public List<User> findByStatus(UserStatus status) {
        return jpaRepository.findAll().stream()
            .filter(entity -> entity.getStatus().equals(status.name()))
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}

