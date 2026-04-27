package com.agora.assemblee.documents.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "document_versions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_document_version_number", columnNames = {"document_id", "version_number"})
        }
)
public class DocumentVersion extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false, length = 500)
    private String storagePath;

    private Long fileSize;

    @Column(length = 150)
    private String contentType;

    @Column(length = 128)
    private String checksum;

    @Column(nullable = false)
    private Boolean currentVersion = Boolean.TRUE;

    @Column(nullable = false)
    private Boolean publishedVersion = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean archivedVersion = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean locked = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;
}