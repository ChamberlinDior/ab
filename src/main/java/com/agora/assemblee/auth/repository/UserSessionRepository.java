package com.agora.assemblee.auth.repository;

import com.agora.assemblee.auth.model.UserSession;
import com.agora.assemblee.common.repository.BaseRepository;

import java.util.Optional;

public interface UserSessionRepository extends BaseRepository<UserSession> {
    Optional<UserSession> findByRefreshToken(String refreshToken);
}
