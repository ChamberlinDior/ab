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
public class VoteRecord extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private VoteSummary voteSummary;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Deputy deputy;
    @Enumerated(EnumType.STRING)
    private VoteChoice voteChoice;
}
