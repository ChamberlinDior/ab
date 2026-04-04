package com.agora.assemblee.workflow.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.enums.WorkflowStatus;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class WorkflowStep extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private WorkflowInstance workflowInstance;
    @Column(nullable = false)
    private Integer stepOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedTo;
    @Enumerated(EnumType.STRING)
    private WorkflowStatus decisionStatus = WorkflowStatus.SUBMITTED;
    private Instant dueAt;
    @Column(columnDefinition = "LONGTEXT")
    private String comment;
}
