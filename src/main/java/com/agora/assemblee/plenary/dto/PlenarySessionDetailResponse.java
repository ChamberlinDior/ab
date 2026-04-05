package com.agora.assemblee.plenary.dto;

import java.util.List;

public record PlenarySessionDetailResponse(
        PlenarySessionResponse session,
        QuorumResponse quorum,
        List<AgendaItemResponse> agendaItems,
        List<SessionAttendanceResponse> attendances,
        List<VoteSummaryResponse> voteSummaries,
        String closingSummary
) {}