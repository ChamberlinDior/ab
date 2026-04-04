package com.agora.assemblee.legislation.model;

import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.documents.model.Document;
import com.agora.assemblee.institution.model.Committee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "commission_reports", indexes = {
        @Index(name = "idx_commission_report_text", columnList = "legislative_text_id"),
        @Index(name = "idx_commission_report_committee", columnList = "committee_id")
})
public class CommissionReport extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "legislative_text_id", nullable = false)
    private LegislativeText legislativeText;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id", nullable = false)
    private Committee committee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_document_id")
    private Document mainDocument;

    @Column(columnDefinition = "LONGTEXT")
    private String recommendationSummary;
}