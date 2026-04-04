package com.agora.assemblee.legislation.model;

import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.Committee;
import com.agora.assemblee.institution.model.Deputy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "amendments", indexes = {
        @Index(name = "idx_amendment_text", columnList = "legislative_text_id"),
        @Index(name = "idx_amendment_article", columnList = "target_article_id"),
        @Index(name = "idx_amendment_author", columnList = "author_deputy_id")
})
public class Amendment extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "legislative_text_id", nullable = false)
    private LegislativeText legislativeText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_article_id")
    private TextArticle targetArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_deputy_id")
    private Deputy authorDeputy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id")
    private Committee committee;

    @Column(nullable = false, length = 120)
    private String amendmentType;

    @Column(columnDefinition = "LONGTEXT")
    private String justification;

    @Column(columnDefinition = "LONGTEXT")
    private String proposedContent;
}