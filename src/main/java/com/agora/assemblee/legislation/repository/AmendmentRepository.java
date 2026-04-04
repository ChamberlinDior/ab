package com.agora.assemblee.legislation.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.legislation.model.Amendment;

import java.util.List;

public interface AmendmentRepository extends BaseRepository<Amendment> {

    List<Amendment> findByLegislativeTextIdOrderByCreatedAtDesc(Long legislativeTextId);

    long countByLegislativeTextId(Long legislativeTextId);
}