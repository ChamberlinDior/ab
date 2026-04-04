package com.agora.assemblee.legislation.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.legislation.model.LegislativeTextVersion;

import java.util.List;
import java.util.Optional;

public interface LegislativeTextVersionRepository extends BaseRepository<LegislativeTextVersion> {

    List<LegislativeTextVersion> findByLegislativeTextIdOrderByVersionNumberDesc(Long legislativeTextId);

    Optional<LegislativeTextVersion> findByLegislativeTextIdAndCurrentVersionTrue(Long legislativeTextId);

    long countByLegislativeTextId(Long legislativeTextId);
}