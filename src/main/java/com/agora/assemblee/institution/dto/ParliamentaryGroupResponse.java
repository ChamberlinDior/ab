package com.agora.assemblee.institution.dto;

public record ParliamentaryGroupResponse(
        Long id,
        String name,
        String acronym,
        String description,
        String leaderName,
        String contactEmail,
        String contactPhone,
        Boolean official,
        long deputiesCount
) {}