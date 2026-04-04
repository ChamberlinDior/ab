package com.agora.assemblee.citizen.model;

import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.legislation.model.LegislativeText;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class PublicConsultation extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private LegislativeText legislativeText;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String publicSummary;
    private Instant opensAt;
    private Instant closesAt;
}
