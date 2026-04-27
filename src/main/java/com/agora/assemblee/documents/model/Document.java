package com.agora.assemblee.documents.model;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.OwnerType;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.documents.enums.DocumentLifecycleStatus;
import com.agora.assemblee.legislation.model.LegislativeText;
import com.agora.assemblee.legislation.model.LegislativeTextVersion;
import com.agora.assemblee.plenary.model.PlenarySession;
import com.agora.assemblee.workflow.model.WorkflowInstance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 100)
    private String documentType;

    @Column(length = 120, unique = true)
    private String referenceNumber;

    @Column(length = 1000)
    private String summary;

    @Column(length = 4000)
    private String description;

    @Column(length = 150)
    private String legalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentClassificationLevel classificationLevel = DocumentClassificationLevel.INTERNAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OwnerType ownerType;

    @Column(nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_status", nullable = false, length = 40)
    private DocumentLifecycleStatus lifecycleStatus = DocumentLifecycleStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legislative_text_id")
    private LegislativeText legislativeText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "text_version_id")
    private LegislativeTextVersion textVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plenary_session_id")
    private PlenarySession plenarySession;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance workflowInstance;

    @Column(nullable = false)
    private Boolean published = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean archived = Boolean.FALSE;

    @Column(nullable = false)
    private Integer currentVersionNumber = 0;

    private Integer publishedVersionNumber;

    @Column(length = 128)
    private String currentChecksum;

    private LocalDate retentionUntil;
    private Instant publishedAt;
    private Instant archivedAt;
    private Instant lockedAt;
}