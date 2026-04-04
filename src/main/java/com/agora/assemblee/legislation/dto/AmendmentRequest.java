package com.agora.assemblee.legislation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AmendmentRequest(
        @NotNull(message = "L'identifiant du texte est obligatoire")
        Long legislativeTextId,

        Long targetArticleId,
        Long authorDeputyId,
        Long committeeId,

        @NotBlank(message = "Le type d'amendement est obligatoire")
        @Size(max = 120, message = "Le type d'amendement ne doit pas dépasser 120 caractères")
        String amendmentType,

        String justification,
        String proposedContent
) {}