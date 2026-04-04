package com.agora.assemblee.legislation.model;

import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.AssemblySession;
import com.agora.assemblee.institution.model.Committee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "text_assignments", indexes = {
        @Index(name = "idx_text_assignment_text", columnList = "legislative_text_id"),
        @Index(name = "idx_text_assignment_committee", columnList = "committee_id"),
        @Index(name = "idx_text_assignment_session", columnList = "assembly_session_id")
})
public class TextAssignment extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "legislative_text_id", nullable = false)
    private LegislativeText legislativeText;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id", nullable = false)
    private Committee committee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assembly_session_id")
    private AssemblySession assemblySession;

    @Column(length = 500)
    private String assignmentReason;
}