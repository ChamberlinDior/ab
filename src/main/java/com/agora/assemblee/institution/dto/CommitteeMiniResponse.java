package com.agora.assemblee.institution.dto;

public record CommitteeMiniResponse(
        Long id,
        String name,
        String code,
        String committeeType,
        Boolean active
) {}