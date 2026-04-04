package com.agora.assemblee.legislation.dto;

import java.time.LocalDateTime;

public record TextAssignmentResponse(
        Long id,
        Long legislativeTextId,
        String legislativeTextTitle,
        Long committeeId,
        String committeeName,
        Long assemblySessionId,
        String assemblySessionLabel,
        String assignmentReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}