package com.agora.assemblee.plenary.dto;

public record VoteSummaryResponse(
        Long id,
        Long agendaItemId,
        String agendaItemLabel,
        String voteMode,
        Integer votesForCount,
        Integer votesAgainstCount,
        Integer abstentionCount,
        Integer absentCount,
        Integer totalVotesCount,
        Boolean adopted,
        String decisionLabel,
        Long adoptedTextVersionId
) {}