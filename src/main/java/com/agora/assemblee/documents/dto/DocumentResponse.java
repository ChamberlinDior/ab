package com.agora.assemblee.documents.dto;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.OwnerType;
import com.agora.assemblee.documents.enums.DocumentLifecycleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DocumentResponse {

    private Long id;
    private String referenceNumber;
    private String title;
    private String documentType;
    private String summary;
    private String description;
    private String legalStatus;
    private DocumentClassificationLevel classificationLevel;
    private OwnerType ownerType;
    private Long ownerId;
    private DocumentLifecycleStatus lifecycleStatus;
    private Boolean published;
    private Boolean archived;
    private Integer currentVersionNumber;
    private Integer publishedVersionNumber;
    private String currentChecksum;
    private LocalDate retentionUntil;
    private Instant publishedAt;
    private Instant archivedAt;
    private Instant lockedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<DocumentVersionResponse> versions;

    /**
     * Nouveaux champs pour la GED intelligente
     */
    private Boolean generatedDocument;
    private Boolean hasInitialVersion;
    private String previewSummary;
    private String previewHtml;
    private String creationMode;
}