package com.agora.assemblee.plenary.dto;

public record UpdateAgendaItemRequest(
        Long legislativeTextId,
        Long commissionReportId,
        String label,
        Integer sortOrder,
        String decisionSummary,
        String status
) {}