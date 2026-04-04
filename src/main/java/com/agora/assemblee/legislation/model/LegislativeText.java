package com.agora.assemblee.legislation.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.LegislativeTextStatus;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.Committee;
import com.agora.assemblee.institution.model.Deputy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "legislative_texts", indexes = {
        @Index(name = "idx_leg_text_filing_number", columnList = "filingNumber", unique = true),
        @Index(name = "idx_leg_text_status", columnList = "workflowStatus"),
        @Index(name = "idx_leg_text_type", columnList = "textType"),
        @Index(name = "idx_leg_text_theme", columnList = "theme")
})
public class LegislativeText extends BaseEntity {

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, unique = true, length = 100)
    private String filingNumber;

    @Column(length = 100)
    private String origin;

    @Column(length = 100)
    private String textType;

    @Column(length = 100)
    private String theme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private LegislativeTextStatus workflowStatus = LegislativeTextStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private DocumentClassificationLevel confidentialityLevel = DocumentClassificationLevel.INTERNAL;

    @Column(length = 4000)
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_committee_id")
    private Committee assignedCommittee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsoring_deputy_id")
    private Deputy sponsoringDeputy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id")
    private LegislativeTextVersion currentVersion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopted_version_id")
    private LegislativeTextVersion adoptedVersion;
}