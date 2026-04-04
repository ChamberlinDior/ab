package com.agora.assemblee.legislation.repository;

import com.agora.assemblee.common.enums.LegislativeTextStatus;
import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.legislation.model.LegislativeText;

import java.util.List;
import java.util.Optional;

public interface LegislativeTextRepository extends BaseRepository<LegislativeText> {

    Optional<LegislativeText> findByFilingNumber(String filingNumber);

    boolean existsByFilingNumber(String filingNumber);

    List<LegislativeText> findByWorkflowStatusOrderByCreatedAtDesc(LegislativeTextStatus status);

    List<LegislativeText> findByAssignedCommitteeIdOrderByCreatedAtDesc(Long committeeId);

    List<LegislativeText> findBySponsoringDeputyIdOrderByCreatedAtDesc(Long deputyId);

    List<LegislativeText> findByTitleContainingIgnoreCaseOrFilingNumberContainingIgnoreCaseOrderByCreatedAtDesc(
            String title,
            String filingNumber
    );

    long countByWorkflowStatus(LegislativeTextStatus status);
}