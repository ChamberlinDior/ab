package com.agora.assemblee.plenary.dto;

public record QuorumResponse(
        Long plenarySessionId,
        Integer expectedMembersCount,
        Integer presentMembersCount,
        Integer quorumThresholdPercent,
        Integer quorumRequiredCount,
        Boolean quorumReached
) {}