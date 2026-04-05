package com.agora.assemblee.plenary.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.plenary.model.VoteSummary;

import java.util.List;
import java.util.Optional;

public interface VoteSummaryRepository extends BaseRepository<VoteSummary> {

    Optional<VoteSummary> findByAgendaItemId(Long agendaItemId);

    List<VoteSummary> findByAgendaItemPlenarySessionId(Long plenarySessionId);
}