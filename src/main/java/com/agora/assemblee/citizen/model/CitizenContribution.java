package com.agora.assemblee.citizen.model;

import com.agora.assemblee.common.enums.ContributionStatus;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.legislation.model.TextArticle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CitizenContribution extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PublicConsultation publicConsultation;
    @ManyToOne(fetch = FetchType.LAZY)
    private CitizenAccount citizenAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    private TextArticle targetArticle;
    @Enumerated(EnumType.STRING)
    private ContributionStatus moderationStatus = ContributionStatus.PENDING_MODERATION;
    @Column(columnDefinition = "LONGTEXT")
    private String contributionText;
}
