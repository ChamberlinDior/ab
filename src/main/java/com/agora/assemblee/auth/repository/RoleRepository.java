package com.agora.assemblee.auth.repository;

import com.agora.assemblee.auth.model.Role;
import com.agora.assemblee.common.repository.BaseRepository;

import java.util.Optional;

public interface RoleRepository extends BaseRepository<Role> {
    Optional<Role> findByName(String name);
}
