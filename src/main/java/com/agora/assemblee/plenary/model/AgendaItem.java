package com.agora.assemblee.plenary.model;

import com.agora.assemblee.legislation.model.CommissionReport;
import com.agora.assemblee.legislation.model.LegislativeText;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AgendaItem extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PlenarySession plenarySession;
    @ManyToOne(fetch = FetchType.LAZY)
    private LegislativeText legislativeText;
    @ManyToOne(fetch = FetchType.LAZY)
    private CommissionReport commissionReport;
    private Integer sortOrder;
    @Column(length = 500)
    private String label;
    @Column(columnDefinition = "LONGTEXT")
    private String decisionSummary;
}
