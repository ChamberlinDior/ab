package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public record UpdatePlenarySessionRequest(
        String title,
        String sessionType,
        Long assemblySessionId,
        Long presidingUserId,
        String location,
        @FutureOrPresent(message = "La date de début doit être présente ou future")
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer expectedMembersCount,
        Integer quorumThresholdPercent
) {}