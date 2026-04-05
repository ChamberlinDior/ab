package com.agora.assemblee.plenary.dto;

public record AgendaItemResponse(
        Long id,
        Long plenarySessionId,
        Long legislativeTextId,
        String legislativeTextTitle,
        Long commissionReportId,
        String commissionReportTitle,
        Integer sortOrder,
        String label,
        String status,
        String decisionSummary
) {}