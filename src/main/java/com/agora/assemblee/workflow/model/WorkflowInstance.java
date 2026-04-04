package com.agora.assemblee.workflow.model;

import com.agora.assemblee.common.enums.WorkflowStatus;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.documents.model.Document;
import com.agora.assemblee.legislation.model.LegislativeText;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class WorkflowInstance extends BaseEntity {
    @Column(nullable = false, length = 120)
    private String workflowType;
    @Enumerated(EnumType.STRING)
    private WorkflowStatus workflowStatus = WorkflowStatus.SUBMITTED;
    @ManyToOne(fetch = FetchType.LAZY)
    private LegislativeText legislativeText;
    @OneToOne(fetch = FetchType.LAZY)
    private Document document;
}
