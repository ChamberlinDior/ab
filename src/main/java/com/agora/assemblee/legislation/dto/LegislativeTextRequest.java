package com.agora.assemblee.legislation.dto;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LegislativeTextRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 250, message = "Le titre ne doit pas dépasser 250 caractères")
        String title,

        @NotBlank(message = "Le numéro de dépôt est obligatoire")
        @Size(max = 100, message = "Le numéro de dépôt ne doit pas dépasser 100 caractères")
        String filingNumber,

        @Size(max = 100, message = "L'origine ne doit pas dépasser 100 caractères")
        String origin,

        @Size(max = 100, message = "Le type de texte ne doit pas dépasser 100 caractères")
        String textType,

        @Size(max = 100, message = "Le thème ne doit pas dépasser 100 caractères")
        String theme,

        @Size(max = 4000, message = "Le résumé ne doit pas dépasser 4000 caractères")
        String summary,

        Long assignedCommitteeId,
        Long sponsoringDeputyId,

        DocumentClassificationLevel confidentialityLevel
) {}