package com.agora.assemblee.legislation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VersionRequest(
        @NotNull(message = "L'identifiant du texte est obligatoire")
        Long legislativeTextId,

        @NotNull(message = "Le numéro de version est obligatoire")
        Integer versionNumber,

        @NotBlank(message = "Le contenu riche est obligatoire")
        String richTextContent,

        @Size(max = 255, message = "Le hash ne doit pas dépasser 255 caractères")
        String contentHash,

        @Size(max = 150, message = "Le libellé de version ne doit pas dépasser 150 caractères")
        String versionLabel,

        Boolean publishable
) {}