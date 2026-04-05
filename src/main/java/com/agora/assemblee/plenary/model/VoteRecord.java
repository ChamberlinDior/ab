package com.agora.assemblee.plenary.model;

import com.agora.assemblee.common.enums.VoteChoice;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.Deputy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "vote_records",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vote_record_summary_deputy", columnNames = {"vote_summary_id", "deputy_id"})
        }
)
public class VoteRecord extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_summary_id", nullable = false)
    private VoteSummary voteSummary;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deputy_id", nullable = false)
    private Deputy deputy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private VoteChoice voteChoice;
}