package com.agora.assemblee.documents.dto;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.OwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DocumentCreateRequest {

    @NotBlank(message = "Le titre du document est obligatoire")
    private String title;

    @NotBlank(message = "Le type du document est obligatoire")
    private String documentType;

    @NotNull(message = "Le niveau de classification est obligatoire")
    private DocumentClassificationLevel classificationLevel;

    @NotNull(message = "Le type de propriétaire est obligatoire")
    private OwnerType ownerType;

    @NotNull(message = "L'identifiant du propriétaire est obligatoire")
    private Long ownerId;

    private String referenceNumber;
    private String summary;
    private String description;
    private String legalStatus;

    private Long legislativeTextId;
    private Long legislativeTextVersionId;
    private Long plenarySessionId;
    private Long workflowInstanceId;

    private LocalDate retentionUntil;

    /**
     * Nouveau comportement :
     * - true  => le document peut être généré automatiquement sans fichier initial
     * - false => comportement classique avec fichier possible
     */
    private Boolean generateA4;

    /**
     * Contenu généré côté front ou côté prévisualisation.
     * Ce champ permet au backend de recevoir un corps déjà préparé.
     */
    private String generatedBody;

    /**
     * Résumé généré côté front si disponible.
     * Si vide, le backend le génère automatiquement.
     */
    private String generatedSummary;

    /**
     * Permet d’indiquer explicitement qu’on crée sans fichier initial.
     */
    private Boolean createWithoutInitialFile;
}