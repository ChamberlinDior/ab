package com.agora.assemblee.institution.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.institution.model.Committee;

import java.util.List;

public interface CommitteeRepository extends BaseRepository<Committee> {
    List<Committee> findAllByOrderByNameAsc();
    List<Committee> findByActiveSessionIdOrderByNameAsc(Long activeSessionId);
    List<Committee> findByActiveTrueOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
    long countByActiveTrue();
}