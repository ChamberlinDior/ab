package com.agora.assemblee.institution.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.institution.model.AssemblySession;

import java.util.List;
import java.util.Optional;

public interface AssemblySessionRepository extends BaseRepository<AssemblySession> {
    List<AssemblySession> findAllByOrderByStartDateDescIdDesc();
    Optional<AssemblySession> findFirstByStatusIgnoreCaseOrderByStartDateDesc(String status);
}