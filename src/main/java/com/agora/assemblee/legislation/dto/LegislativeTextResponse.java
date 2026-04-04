package com.agora.assemblee.legislation.dto;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.LegislativeTextStatus;

import java.time.Instant;

public record LegislativeTextResponse(
        Long id,
        String title,
        String filingNumber,
        String origin,
        String textType,
        String theme,
        String summary,
        LegislativeTextStatus workflowStatus,
        DocumentClassificationLevel confidentialityLevel,
        Long assignedCommitteeId,
        String assignedCommitteeName,
        Long sponsoringDeputyId,
        String sponsoringDeputyName,
        Long currentVersionId,
        Integer currentVersionNumber,
        Long adoptedVersionId,
        Integer adoptedVersionNumber,
        Integer totalVersions,
        Integer totalArticles,
        Integer totalAmendments,
        Integer totalReports,
        Integer totalAssignments,
        Instant createdAt,
        Instant updatedAt
) {}