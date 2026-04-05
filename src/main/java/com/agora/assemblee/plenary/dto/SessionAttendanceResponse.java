package com.agora.assemblee.plenary.dto;

public record SessionAttendanceResponse(
        Long id,
        Long plenarySessionId,
        Long deputyId,
        String deputyName,
        String attendanceStatus,
        Boolean present,
        String absenceJustification,
        Long recordedByUserId,
        String recordedByName
) {}