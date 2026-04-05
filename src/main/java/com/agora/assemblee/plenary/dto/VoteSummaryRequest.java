package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.NotBlank;

public record VoteSummaryRequest(
        @NotBlank(message = "Le mode de vote est obligatoire")
        String voteMode,

        Long adoptedTextVersionId,

        String decisionLabel
) {}