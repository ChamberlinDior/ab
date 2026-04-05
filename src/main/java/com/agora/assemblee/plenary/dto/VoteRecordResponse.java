package com.agora.assemblee.plenary.dto;

public record VoteRecordResponse(
        Long id,
        Long voteSummaryId,
        Long deputyId,
        String deputyName,
        String voteChoice
) {}