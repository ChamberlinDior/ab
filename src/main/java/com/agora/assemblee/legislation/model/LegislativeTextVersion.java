package com.agora.assemblee.legislation.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "legislative_text_versions", indexes = {
        @Index(name = "idx_leg_text_version_text", columnList = "legislative_text_id"),
        @Index(name = "idx_leg_text_version_number", columnList = "versionNumber")
})
public class LegislativeTextVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "legislative_text_id", nullable = false)
    private LegislativeText legislativeText;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(length = 150)
    private String versionLabel;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String richTextContent;

    @Column(length = 255)
    private String contentHash;

    @Column(nullable = false)
    private Boolean currentVersion = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean publishable = Boolean.FALSE;
}