package com.agora.assemblee.documents.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DocumentVersion extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Document document;
    @Column(nullable = false)
    private Integer versionNumber;
    @Column(nullable = false, length = 255)
    private String originalFilename;
    @Column(nullable = false, length = 255)
    private String storagePath;
    private Long fileSize;
    private String contentType;
    private String checksum;
    private Boolean currentVersion = Boolean.TRUE;
    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;
}
