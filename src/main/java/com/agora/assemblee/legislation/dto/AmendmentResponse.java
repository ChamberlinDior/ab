package com.agora.assemblee.legislation.dto;

import java.time.Instant;

public record AmendmentResponse(
        Long id,
        Long legislativeTextId,
        String legislativeTextTitle,
        Long targetArticleId,
        String targetArticleNumber,
        Long authorDeputyId,
        String authorDeputyName,
        Long committeeId,
        String committeeName,
        String amendmentType,
        String justification,
        String proposedContent,
        Instant createdAt,
        Instant updatedAt
) {}