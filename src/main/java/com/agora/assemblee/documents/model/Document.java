package com.agora.assemblee.documents.model;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.OwnerType;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.legislation.model.LegislativeText;
import com.agora.assemblee.legislation.model.LegislativeTextVersion;
import com.agora.assemblee.plenary.model.PlenarySession;
import com.agora.assemblee.workflow.model.WorkflowInstance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Document extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String title;
    private String documentType;
    @Enumerated(EnumType.STRING)
    private DocumentClassificationLevel classificationLevel = DocumentClassificationLevel.INTERNAL;
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;
    private Long ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private LegislativeText legislativeText;

    @ManyToOne(fetch = FetchType.LAZY)
    private LegislativeTextVersion textVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlenarySession plenarySession;

    @OneToOne(fetch = FetchType.LAZY)
    private WorkflowInstance workflowInstance;

    private Boolean published = Boolean.FALSE;
    private Boolean archived = Boolean.FALSE;
}
