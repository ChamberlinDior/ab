package com.agora.assemblee.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommitteeRequest(
        @NotBlank String name,
        String code,
        String committeeType,
        String thematicScope,
        String description,
        String room,
        String contactEmail,
        String contactPhone,
        Boolean active,
        @NotNull Long activeSessionId
) {}