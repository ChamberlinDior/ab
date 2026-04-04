package com.agora.assemblee.institution.dto;

public record InstitutionSummaryResponse(
        long totalDeputies,
        long activeDeputies,
        long totalCommittees,
        long activeCommittees,
        long totalMemberships,
        long totalGroups,
        AssemblySessionMiniResponse activeSession
) {}