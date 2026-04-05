package com.agora.assemblee.plenary.model;

import com.agora.assemblee.common.enums.VoteMode;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.legislation.model.LegislativeTextVersion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vote_summaries")
public class VoteSummary extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_item_id", nullable = false, unique = true)
    private AgendaItem agendaItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private VoteMode voteMode;

    @Column(nullable = false)
    private Integer votesForCount = 0;

    @Column(nullable = false)
    private Integer votesAgainstCount = 0;

    @Column(nullable = false)
    private Integer abstentionCount = 0;

    @Column(nullable = false)
    private Integer absentCount = 0;

    @Column(nullable = false)
    private Boolean adopted = Boolean.FALSE;

    @Column(length = 180)
    private String decisionLabel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopted_text_version_id")
    private LegislativeTextVersion adoptedTextVersion;
}