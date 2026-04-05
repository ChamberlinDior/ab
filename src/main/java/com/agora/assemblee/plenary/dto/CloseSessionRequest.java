package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.NotBlank;

public record CloseSessionRequest(
        @NotBlank(message = "Le résumé de clôture est obligatoire")
        String closingSummary
) {}