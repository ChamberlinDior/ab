package com.agora.assemblee.plenary.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.plenary.model.VoteRecord;

import java.util.List;
import java.util.Optional;

public interface VoteRecordRepository extends BaseRepository<VoteRecord> {

    Optional<VoteRecord> findByVoteSummaryIdAndDeputyId(Long voteSummaryId, Long deputyId);

    List<VoteRecord> findByVoteSummaryId(Long voteSummaryId);
}