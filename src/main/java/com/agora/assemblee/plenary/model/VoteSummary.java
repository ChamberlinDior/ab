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
public class VoteSummary extends BaseEntity {
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private AgendaItem agendaItem;
    @Enumerated(EnumType.STRING)
    private VoteMode voteMode;
    private Integer votesForCount = 0;
    private Integer votesAgainstCount = 0;
    private Integer abstentionCount = 0;
    private Integer absentCount = 0;
    private Boolean adopted = Boolean.FALSE;
    @ManyToOne(fetch = FetchType.LAZY)
    private LegislativeTextVersion adoptedTextVersion;
}
