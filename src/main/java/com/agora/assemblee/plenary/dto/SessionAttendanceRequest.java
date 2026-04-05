package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.NotNull;

public record SessionAttendanceRequest(
        @NotNull(message = "Le député est obligatoire")
        Long deputyId,

        @NotNull(message = "Le statut de présence est obligatoire")
        String attendanceStatus,

        String absenceJustification,

        Long recordedByUserId
) {}