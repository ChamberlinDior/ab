package com.agora.assemblee.legislation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TextAssignmentRequest(
        @NotNull(message = "L'identifiant du texte est obligatoire")
        Long legislativeTextId,

        @NotNull(message = "L'identifiant de la commission est obligatoire")
        Long committeeId,

        Long assemblySessionId,

        @Size(max = 500, message = "Le motif d'affectation ne doit pas dépasser 500 caractères")
        String assignmentReason
) {}