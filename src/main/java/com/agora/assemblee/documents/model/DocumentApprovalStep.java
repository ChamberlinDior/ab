package com.agora.assemblee.documents.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.documents.enums.DocumentApprovalDecision;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "document_approval_steps")
public class DocumentApprovalStep extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_version_id", nullable = false)
    private DocumentVersion documentVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentApprovalDecision decision;

    @Column(nullable = false, length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by_id")
    private User decidedBy;
}