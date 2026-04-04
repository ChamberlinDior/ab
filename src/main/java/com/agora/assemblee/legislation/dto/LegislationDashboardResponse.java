package com.agora.assemblee.legislation.dto;

public record LegislationDashboardResponse(
        long totalTexts,
        long totalDrafts,
        long totalUnderReview,
        long totalAdopted,
        long totalRejected,
        long totalPublished,
        long totalArchived,
        long totalAssignments,
        long totalReports,
        long totalAmendments
) {}