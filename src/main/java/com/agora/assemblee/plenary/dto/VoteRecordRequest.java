package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRecordRequest(
        @NotNull(message = "Le député est obligatoire")
        Long deputyId,

        @NotNull(message = "Le choix de vote est obligatoire")
        String choice
) {}