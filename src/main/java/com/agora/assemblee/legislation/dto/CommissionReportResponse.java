package com.agora.assemblee.legislation.dto;

import java.time.Instant;

public record CommissionReportResponse(
        Long id,
        Long legislativeTextId,
        String legislativeTextTitle,
        Long committeeId,
        String committeeName,
        Long mainDocumentId,
        String recommendationSummary,
        Instant createdAt,
        Instant updatedAt
) {}