package com.agora.assemblee.legislation.dto;

import jakarta.validation.constraints.NotNull;

public record CommissionReportRequest(
        @NotNull(message = "L'identifiant du texte est obligatoire")
        Long legislativeTextId,

        @NotNull(message = "L'identifiant de la commission est obligatoire")
        Long committeeId,

        Long mainDocumentId,
        String recommendationSummary
) {}