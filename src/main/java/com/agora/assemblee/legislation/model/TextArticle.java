package com.agora.assemblee.legislation.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "text_articles", indexes = {
        @Index(name = "idx_text_article_version", columnList = "text_version_id"),
        @Index(name = "idx_text_article_number", columnList = "articleNumber")
})
public class TextArticle extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "text_version_id", nullable = false)
    private LegislativeTextVersion textVersion;

    @Column(nullable = false, length = 60)
    private String articleNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private Integer sortOrder = 0;
}