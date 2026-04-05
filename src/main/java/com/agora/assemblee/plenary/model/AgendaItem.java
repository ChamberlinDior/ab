package com.agora.assemblee.plenary.model;

import com.agora.assemblee.common.enums.AgendaItemStatus;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.legislation.model.CommissionReport;
import com.agora.assemblee.legislation.model.LegislativeText;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "agenda_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_agenda_item_session_sort_order",
                        columnNames = {"plenary_session_id", "sort_order"}
                )
        }
)
public class AgendaItem extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "plenary_session_id", nullable = false)
    private PlenarySession plenarySession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legislative_text_id")
    private LegislativeText legislativeText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_report_id")
    private CommissionReport commissionReport;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(nullable = false, length = 500)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "agenda_item_status", nullable = false, length = 40)
    private AgendaItemStatus agendaItemStatus = AgendaItemStatus.PLANNED;

    @Column(columnDefinition = "LONGTEXT")
    private String decisionSummary;
}