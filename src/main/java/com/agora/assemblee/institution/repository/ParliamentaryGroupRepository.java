package com.agora.assemblee.institution.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.institution.model.ParliamentaryGroup;

import java.util.List;

public interface ParliamentaryGroupRepository extends BaseRepository<ParliamentaryGroup> {
    List<ParliamentaryGroup> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}