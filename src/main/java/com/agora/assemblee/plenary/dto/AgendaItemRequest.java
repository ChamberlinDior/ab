package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AgendaItemRequest(
        Long legislativeTextId,
        Long commissionReportId,

        @NotBlank(message = "Le libellé du point est obligatoire")
        String label,

        @Min(value = 1, message = "L'ordre doit être supérieur à 0")
        Integer sortOrder
) {}