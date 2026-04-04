package com.agora.assemblee.institution.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_committee_membership_unique",
                columnNames = {"committee_id", "deputy_id", "assembly_session_id"}
        )
)
public class CommitteeMembership extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id", nullable = false)
    private Committee committee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deputy_id", nullable = false)
    private Deputy deputy;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assembly_session_id", nullable = false)
    private AssemblySession assemblySession;

    @Column(nullable = false, length = 80)
    private String officeRole;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean primaryMembership = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean bureauMember = Boolean.FALSE;

    @Column(length = 1200)
    private String notes;
}