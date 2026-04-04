package com.agora.assemblee.institution.dto;

import jakarta.validation.constraints.NotBlank;

public record ParliamentaryGroupRequest(
        @NotBlank String name,
        String acronym,
        String description,
        String leaderName,
        String contactEmail,
        String contactPhone,
        Boolean official
) {}