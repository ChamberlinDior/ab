package com.agora.assemblee.plenary.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.plenary.model.AgendaItem;

import java.util.List;

public interface AgendaItemRepository extends BaseRepository<AgendaItem> {

    List<AgendaItem> findByPlenarySessionIdOrderBySortOrderAsc(Long plenarySessionId);

    long countByPlenarySessionId(Long plenarySessionId);

    boolean existsByPlenarySessionIdAndSortOrder(Long plenarySessionId, Integer sortOrder);
}