package com.agora.assemblee.auth.repository;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
}
